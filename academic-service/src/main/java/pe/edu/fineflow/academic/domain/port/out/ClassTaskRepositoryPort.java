package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ClassTask;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface ClassTaskRepositoryPort extends BaseTenantRepositoryPort<ClassTask> {
    Flux<ClassTask> findAllByCourseAssignmentId(String courseAssignmentId);
}
