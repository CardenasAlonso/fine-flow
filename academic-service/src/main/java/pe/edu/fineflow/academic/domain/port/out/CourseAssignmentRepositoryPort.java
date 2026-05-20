package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.CourseAssignment;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface CourseAssignmentRepositoryPort extends BaseTenantRepositoryPort<CourseAssignment> {
    Flux<CourseAssignment> findAllBySectionId(String sectionId);
}
