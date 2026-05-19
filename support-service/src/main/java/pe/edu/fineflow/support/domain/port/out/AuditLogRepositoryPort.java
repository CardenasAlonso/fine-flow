package pe.edu.fineflow.support.domain.port.out;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.support.domain.model.AuditLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuditLogRepositoryPort {
    Mono<AuditLog> save(AuditLog log);

    Flux<AuditLog> findBySchoolIdAndAction(String schoolId, String action);

    Flux<AuditLog> findBySchoolIdAndUserId(String schoolId, String userId);

    Flux<AuditLog> findBySchoolId(String schoolId, Pageable pageable);

    Flux<AuditLog> findBySchoolIdAndAction(String schoolId, String action, Pageable pageable);

    Flux<AuditLog> findBySchoolIdAndUserId(String schoolId, String userId, Pageable pageable);

    Flux<AuditLog> findAll(Pageable pageable);

    Flux<AuditLog> findByAction(String action, Pageable pageable);

    Flux<AuditLog> findByUserId(String userId, Pageable pageable);
}
