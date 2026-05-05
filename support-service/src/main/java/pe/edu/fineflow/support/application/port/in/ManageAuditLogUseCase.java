package pe.edu.fineflow.support.application.port.in;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.support.domain.model.AuditLog;
import reactor.core.publisher.Flux;

public interface ManageAuditLogUseCase {
    Flux<AuditLog> findAll(Pageable pageable);

    Flux<AuditLog> findByAction(String action, Pageable pageable);

    Flux<AuditLog> findByUserId(String userId, Pageable pageable);
}
