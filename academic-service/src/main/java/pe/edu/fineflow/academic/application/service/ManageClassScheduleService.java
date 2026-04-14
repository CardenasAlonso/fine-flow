package pe.edu.fineflow.academic.application.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageClassScheduleUseCase;
import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import pe.edu.fineflow.academic.domain.port.out.ClassScheduleRepositoryPort;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageClassScheduleService implements ManageClassScheduleUseCase {
    private final ClassScheduleRepositoryPort repository;

    @Override
    public Mono<ClassSchedule> create(ClassSchedule classSchedule) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId -> {
                            classSchedule.setId(UuidGenerator.generate());
                            classSchedule.setSchoolId(schoolId);
                            classSchedule.setIsActive(1);
                            classSchedule.setCreatedAt(Instant.now());
                            return repository.save(classSchedule);
                        });
    }

    @Override
    public Mono<ClassSchedule> update(String id, ClassSchedule updated) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setClassroomId(updated.getClassroomId());
                            existing.setTimeSlotId(updated.getTimeSlotId());
                            existing.setDayOfWeek(updated.getDayOfWeek());
                            existing.setWeekType(updated.getWeekType());
                            existing.setColorHex(updated.getColorHex());
                            existing.setNotes(updated.getNotes());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<ClassSchedule> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<ClassSchedule> findByScheduleVersion(String scheduleVersionId) {
        return repository.findAllByScheduleVersionId(scheduleVersionId);
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
