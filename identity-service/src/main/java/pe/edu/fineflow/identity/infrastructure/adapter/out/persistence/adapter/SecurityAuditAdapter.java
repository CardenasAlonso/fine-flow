package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.identity.domain.port.out.SecurityAuditPort;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.SecurityEventEntity;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository.SecurityEventR2dbcRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityAuditAdapter implements SecurityAuditPort {

    private final SecurityEventR2dbcRepository repository;

    @Override
    public Mono<Void> record(Event event) {
        return repository.save(toEntity(event)).then();
    }

    @Override
    public Mono<Void> recordAndForget(Event event) {
        return record(event)
                .doOnError(
                        e -> log.warn("No se pudo registrar evento de seguridad: {}", e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    private SecurityEventEntity toEntity(Event e) {
        SecurityEventEntity entity = new SecurityEventEntity();
        entity.setId(UuidGenerator.generate());
        entity.setSchoolId(e.schoolId());
        entity.setUserId(e.userId());
        entity.setEventType(e.eventType());
        entity.setSeverity(e.severity());
        entity.setDescription(e.description());
        entity.setIpAddress(e.ipAddress());
        entity.setUserAgent(e.userAgent());
        entity.setMetadataJson(e.metadataJson());
        entity.setResolved(0);
        entity.setCreatedAt(Instant.now());
        return entity;
    }
}