package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.support.domain.model.SecurityEvent;
import pe.edu.fineflow.support.domain.port.out.SecurityEventRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.SecurityEventEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.SecurityEventR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityEventRepositoryAdapter implements SecurityEventRepositoryPort {

    private final SecurityEventR2dbcRepository repository;

    @Override
    public Mono<SecurityEvent> save(SecurityEvent event) {
        return repository.save(toEntity(event)).map(this::toModel);
    }

    @Override
    public Mono<SecurityEvent> findById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public Flux<SecurityEvent> findBySchoolId(String schoolId) {
        return repository.findBySchoolIdOrderByCreatedAtDesc(schoolId).map(this::toModel);
    }

    @Override
    public Flux<SecurityEvent> findUnresolvedBySeverity(String severity) {
        return repository.findBySeverityAndResolved(severity, 0).map(this::toModel);
    }

    @Override
    public Mono<Void> markResolved(String id, String resolvedBy) {
        return repository
                .findById(id)
                .flatMap(
                        e -> {
                            e.setResolved(1);
                            e.setResolvedBy(resolvedBy);
                            e.setResolvedAt(Instant.now());
                            return repository.save(e);
                        })
                .then();
    }

    private SecurityEventEntity toEntity(SecurityEvent m) {
        SecurityEventEntity e = new SecurityEventEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setUserId(m.getUserId());
        e.setEventType(m.getEventType());
        e.setSeverity(m.getSeverity());
        e.setDescription(m.getDescription());
        e.setIpAddress(m.getIpAddress());
        e.setUserAgent(m.getUserAgent());
        e.setSessionToken(m.getSessionToken());
        e.setEntityType(m.getEntityType());
        e.setEntityId(m.getEntityId());
        e.setMetadataJson(m.getMetadataJson());
        e.setResolved(m.getResolved() != null && m.getResolved() == 1 ? 1 : 0);
        e.setResolvedBy(m.getResolvedBy());
        e.setResolvedAt(m.getResolvedAt());
        return e;
    }

    private SecurityEvent toModel(SecurityEventEntity e) {
        SecurityEvent m = new SecurityEvent();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setUserId(e.getUserId());
        m.setEventType(e.getEventType());
        m.setSeverity(e.getSeverity());
        m.setDescription(e.getDescription());
        m.setIpAddress(e.getIpAddress());
        m.setUserAgent(e.getUserAgent());
        m.setSessionToken(e.getSessionToken());
        m.setEntityType(e.getEntityType());
        m.setEntityId(e.getEntityId());
        m.setMetadataJson(e.getMetadataJson());
        m.setResolved(e.getResolved());
        m.setResolvedBy(e.getResolvedBy());
        m.setResolvedAt(e.getResolvedAt());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}