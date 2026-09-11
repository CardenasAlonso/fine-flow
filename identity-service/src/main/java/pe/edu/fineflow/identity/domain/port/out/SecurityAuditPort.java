package pe.edu.fineflow.identity.domain.port.out;

import java.time.Instant;
import reactor.core.publisher.Mono;

public interface SecurityAuditPort {

    record Event(
            String schoolId,
            String userId,
            String eventType,
            String severity,
            String description,
            String ipAddress,
            String userAgent,
            String metadataJson) {}

    Mono<Void> record(Event event);

    Mono<Void> recordAndForget(Event event);
}