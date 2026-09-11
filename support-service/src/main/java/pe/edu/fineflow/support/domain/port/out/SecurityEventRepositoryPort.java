package pe.edu.fineflow.support.domain.port.out;

import pe.edu.fineflow.support.domain.model.SecurityEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SecurityEventRepositoryPort {
    Mono<SecurityEvent> save(SecurityEvent event);

    Mono<SecurityEvent> findById(String id);

    Flux<SecurityEvent> findBySchoolId(String schoolId);

    Flux<SecurityEvent> findUnresolvedBySeverity(String severity);

    Mono<Void> markResolved(String id, String resolvedBy);
}