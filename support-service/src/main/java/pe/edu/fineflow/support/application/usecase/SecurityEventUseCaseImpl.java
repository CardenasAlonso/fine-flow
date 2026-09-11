package pe.edu.fineflow.support.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.application.port.in.ManageSecurityEventUseCase;
import pe.edu.fineflow.support.domain.model.SecurityEvent;
import pe.edu.fineflow.support.domain.port.out.SecurityEventRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SecurityEventUseCaseImpl implements ManageSecurityEventUseCase {

    private final SecurityEventRepositoryPort repo;

    public SecurityEventUseCaseImpl(SecurityEventRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<SecurityEvent> log(SecurityEvent event) {
        event.setId(UuidGenerator.generate());
        event.setCreatedAt(Instant.now());
        if (event.getResolved() == null) {
            event.setResolved(0);
        }
        return repo.save(event);
    }

    @Override
    public Mono<SecurityEvent> log(
            String schoolId, String userId, String eventType, String severity,
            String description, String ipAddress, String metadataJson) {
        SecurityEvent event = new SecurityEvent();
        event.setId(UuidGenerator.generate());
        event.setSchoolId(schoolId);
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setSeverity(severity);
        event.setDescription(description);
        event.setIpAddress(ipAddress);
        event.setMetadataJson(metadataJson);
        event.setResolved(0);
        event.setCreatedAt(Instant.now());
        return repo.save(event);
    }

    @Override
    public Flux<SecurityEvent> findByMySchool() {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findBySchoolId(sid));
    }

    @Override
    public Flux<SecurityEvent> findUnresolvedBySeverity(String severity) {
        return repo.findUnresolvedBySeverity(severity);
    }

    @Override
    public Mono<Void> markResolved(String id) {
        return TenantContext.getPrincipal()
                .flatMap(p -> repo.markResolved(id, p.userId()));
    }
}