package pe.edu.fineflow.profile.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.fineflow.common.event.EventBus;
import pe.edu.fineflow.common.event.StudentEnrolledEvent;
import pe.edu.fineflow.common.exception.BusinessException;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.profile.application.port.in.ManageStudentUseCase;
import pe.edu.fineflow.profile.domain.model.Student;
import pe.edu.fineflow.profile.domain.port.out.StudentRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ManageStudentService extends BaseTenantService<Student> implements ManageStudentUseCase {

    private final StudentRepositoryPort repo;
    private final EventBus eventBus;

    public ManageStudentService(StudentRepositoryPort repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    @Override
    protected BaseTenantRepositoryPort<Student> getRepository() { return repo; }

    @Override
    protected String entityName() { return "Student"; }

    @Override
    protected void applyUpdate(Student existing, Student updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSectionId(updated.getSectionId());
    }

    @Override
    @Transactional
    public Mono<Student> create(Student student) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId ->
                                repo.existsByDocumentNumberAndSchoolId(
                                                student.getDocumentNumber(), schoolId)
                                        .flatMap(
                                                exists -> {
                                                    if (exists)
                                                        return Mono.error(
                                                                BusinessException.conflict(
                                                                        "STUDENT_DUPLICATE",
                                                                        "Ya existe un alumno con"
                                                                                + " ese DNI."));
                                                    student.setStatus("ACTIVE");
                                                    return super.create(student);
                                                })
                                        .doOnSuccess(
                                                s -> {
                                                    if (s.getSectionId() != null)
                                                        eventBus.publish(
                                                                new StudentEnrolledEvent(
                                                                        schoolId,
                                                                        "system",
                                                                        s.getId(),
                                                                        s.getSectionId()));
                                                }));
    }

    @Override
    public Flux<Student> findAllBySection(String sectionId) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findAllBySectionIdAndSchoolId(sectionId, sid));
    }

    @Override
    public Flux<Student> search(String q) {
        return TenantContext.getSchoolId().flatMapMany(sid -> repo.searchBySchoolId(sid, q));
    }
}