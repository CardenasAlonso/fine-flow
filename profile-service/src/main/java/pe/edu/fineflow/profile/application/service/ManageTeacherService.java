package pe.edu.fineflow.profile.application.service;

import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.BusinessException;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.profile.application.port.in.ManageTeacherUseCase;
import pe.edu.fineflow.profile.domain.model.Teacher;
import pe.edu.fineflow.profile.domain.port.out.TeacherRepositoryPort;
import reactor.core.publisher.Mono;

@Service
public class ManageTeacherService extends BaseTenantService<Teacher> implements ManageTeacherUseCase {
    private final TeacherRepositoryPort repository;

    public ManageTeacherService(TeacherRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    protected BaseTenantRepositoryPort<Teacher> getRepository() { return repository; }

    @Override
    protected String entityName() { return "Teacher"; }

    @Override
    protected void applyUpdate(Teacher existing, Teacher updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialty(updated.getSpecialty());
    }

    @Override
    public Mono<Teacher> create(Teacher t) {
        return TenantContext.getSchoolId()
                .flatMap(sid ->
                        repository.existsByDocumentNumberAndSchoolId(t.getDocumentNumber(), sid)
                                .flatMap(ex -> {
                                    if (ex)
                                        return Mono.error(BusinessException.conflict(
                                                "TEACHER_DUPLICATE",
                                                "Ya existe un docente con ese DNI."));
                                    t.setStatus("ACTIVE");
                                    return super.create(t);
                                }));
    }
}
