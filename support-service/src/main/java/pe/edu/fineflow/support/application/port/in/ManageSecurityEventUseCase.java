package pe.edu.fineflow.support.application.port.in;

import pe.edu.fineflow.support.domain.model.SecurityEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageSecurityEventUseCase {
    Mono<SecurityEvent> log(SecurityEvent event);

    Mono<SecurityEvent> log(
            String schoolId, String userId, String eventType, String severity,
            String description, String ipAddress, String metadataJson);

    Flux<SecurityEvent> findByMySchool();

    Flux<SecurityEvent> findUnresolvedBySeverity(String severity);

    Mono<Void> markResolved(String id);
}