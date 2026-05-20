package pe.edu.fineflow.profile.domain.port.out;

import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.profile.domain.model.Teacher;
import reactor.core.publisher.Mono;

public interface TeacherRepositoryPort extends BaseTenantRepositoryPort<Teacher> {
    Mono<Boolean> existsByDocumentNumberAndSchoolId(String documentNumber, String schoolId);
}
