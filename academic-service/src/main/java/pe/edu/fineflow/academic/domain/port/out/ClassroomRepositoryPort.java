package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface ClassroomRepositoryPort extends BaseTenantRepositoryPort<Classroom> {
    Flux<Classroom> findAllActiveBySchoolId(String schoolId);
}
