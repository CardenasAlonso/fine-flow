package pe.edu.fineflow.academic.application.service;

import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageClassTaskUseCase;
import pe.edu.fineflow.academic.domain.model.ClassTask;
import pe.edu.fineflow.academic.domain.port.out.ClassTaskRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Flux;

@Service
public class ManageClassTaskService extends BaseTenantService<ClassTask> implements ManageClassTaskUseCase {
    private final ClassTaskRepositoryPort repository;

    public ManageClassTaskService(ClassTaskRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    protected BaseTenantRepositoryPort<ClassTask> getRepository() { return repository; }

    @Override
    protected String entityName() { return "ClassTask"; }

    @Override
    protected void applyUpdate(ClassTask existing, ClassTask updated) {
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setTaskType(updated.getTaskType());
        existing.setMaxScore(updated.getMaxScore());
        existing.setDueDate(updated.getDueDate());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    public Flux<ClassTask> findAll(int offset, int limit) {
        return super.findAll(offset, limit);
    }

    @Override
    public Flux<ClassTask> findByCourseAssignment(String courseAssignmentId) {
        return repository.findAllByCourseAssignmentId(courseAssignmentId);
    }
}
