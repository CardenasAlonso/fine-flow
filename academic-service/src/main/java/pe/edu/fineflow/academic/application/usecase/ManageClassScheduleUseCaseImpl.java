package pe.edu.fineflow.academic.application.usecase;

import java.time.Instant;

import pe.edu.fineflow.academic.application.port.in.ManageClassScheduleUseCase;
import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.academic.domain.port.out.ClassScheduleRepositoryPort;
import pe.edu.fineflow.academic.domain.port.out.TimeSlotRepositoryPort;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ManageClassScheduleUseCaseImpl extends BaseTenantService<ClassSchedule> implements ManageClassScheduleUseCase {
    private final ClassScheduleRepositoryPort repository;
    private final TimeSlotRepositoryPort timeSlotRepository;

    public ManageClassScheduleUseCaseImpl(ClassScheduleRepositoryPort repository,
            TimeSlotRepositoryPort timeSlotRepository) {
        this.repository = repository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    protected BaseTenantRepositoryPort<ClassSchedule> getRepository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "ClassSchedule";
    }

    @Override
    protected void applyUpdate(ClassSchedule existing, ClassSchedule updated) {
    }

    @Override
    public Mono<ClassSchedule> create(ClassSchedule classSchedule) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId -> timeSlotRepository
                                .findByIdAndSchoolId(classSchedule.getTimeSlotId(), schoolId)
                                .flatMap(
                                        slot -> {
                                            if (slot.isBreak()) {
                                                return Mono.error(
                                                        new IllegalArgumentException(
                                                                "No se puede asignar una"
                                                                        + " clase a un slot de"
                                                                        + " descanso"));
                                            }
                                            classSchedule.setId(UuidGenerator.generate());
                                            classSchedule.setSchoolId(schoolId);
                                            classSchedule.setIsActive(1);
                                            classSchedule.setCreatedAt(Instant.now());
                                            return repository.save(classSchedule);
                                        })
                                .switchIfEmpty(
                                        Mono.error(
                                                new IllegalArgumentException(
                                                        "TimeSlot no encontrado: "
                                                                + classSchedule
                                                                        .getTimeSlotId()))));
    }

    @Override
    public Mono<ClassSchedule> update(String id, ClassSchedule updated) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId -> getRepository().findByIdAndSchoolId(id, schoolId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(entityName(), id)))
                        .flatMap(existing -> timeSlotRepository
                                .findByIdAndSchoolId(updated.getTimeSlotId(), schoolId)
                                .flatMap(slot -> {
                                    if (slot.isBreak()) {
                                        return Mono.error(
                                                new IllegalArgumentException(
                                                        "No se puede asignar una"
                                                                + " clase a un slot de"
                                                                + " descanso"));
                                    }
                                    existing.applyFrom(updated);
                                    return repository.save(existing);
                                })
                                .switchIfEmpty(
                                        Mono.error(
                                                new IllegalArgumentException(
                                                        "TimeSlot no encontrado: "
                                                                + updated
                                                                        .getTimeSlotId())))));
    }

    @Override
    public Flux<ClassSchedule> findByScheduleVersion(String scheduleVersionId) {
        return TenantContext.getSchoolId()
                .flatMapMany(
                        schoolId -> repository
                                .findAllByScheduleVersionId(scheduleVersionId)
                                .filter(cs -> schoolId.equals(cs.getSchoolId())));
    }

    @Override
    public Flux<ClassSchedule> findByTeacher(String teacherId) {
        return repository.findByTeacherId(teacherId);
    }

    @Override
    public Flux<ClassSchedule> findBySection(String sectionId) {
        return repository.findBySectionId(sectionId);
    }
}
