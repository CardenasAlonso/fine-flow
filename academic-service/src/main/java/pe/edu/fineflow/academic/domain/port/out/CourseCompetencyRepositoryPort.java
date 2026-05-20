package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.CourseCompetency;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface CourseCompetencyRepositoryPort extends BaseTenantRepositoryPort<CourseCompetency> {
    Flux<CourseCompetency> findAllByCourseId(String courseId);
}
