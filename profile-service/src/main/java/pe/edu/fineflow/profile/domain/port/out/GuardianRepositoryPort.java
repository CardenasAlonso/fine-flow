package pe.edu.fineflow.profile.domain.port.out;

import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.profile.domain.model.Guardian;
import reactor.core.publisher.Flux;

public interface GuardianRepositoryPort extends BaseTenantRepositoryPort<Guardian> {
    Flux<Guardian> findAllByStudentId(String studentId);
}
