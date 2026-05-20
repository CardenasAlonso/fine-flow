package pe.edu.fineflow.profile.domain.port.out;

import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.profile.domain.model.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentRepositoryPort extends BaseTenantRepositoryPort<Student> {
    Flux<Student> findAllBySectionIdAndSchoolId(String sectionId, String schoolId);

    Mono<Boolean> existsByDocumentNumberAndSchoolId(String documentNumber, String schoolId);

    Flux<Student> searchBySchoolId(String schoolId, String query);
}
