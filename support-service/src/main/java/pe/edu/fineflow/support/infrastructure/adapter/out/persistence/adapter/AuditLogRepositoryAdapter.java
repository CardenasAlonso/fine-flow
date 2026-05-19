package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.support.domain.model.AuditLog;
import pe.edu.fineflow.support.domain.port.out.AuditLogRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.AuditLogR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {

    private final AuditLogR2dbcRepository repository;

    @Override
    public Mono<AuditLog> save(AuditLog log) {
        return repository.save(toEntity(log)).map(this::toModel);
    }

    @Override
    public Flux<AuditLog> findBySchoolIdAndAction(String schoolId, String action) {
        return repository.findBySchoolIdAndAction(schoolId, action).map(this::toModel);
    }

    @Override
    public Flux<AuditLog> findBySchoolIdAndUserId(String schoolId, String userId) {
        return repository.findBySchoolIdAndUserId(schoolId, userId).map(this::toModel);
    }

    @Override
    public Flux<AuditLog> findAll(Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        return repository.findAll(offset, limit).map(this::toModel);
    }

    @Override
    public Flux<AuditLog> findByAction(String action, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        return repository.findByAction(action, offset, limit).map(this::toModel);
    }

    @Override
    public Flux<AuditLog> findByUserId(String userId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        return repository.findByUserId(userId, offset, limit).map(this::toModel);
    }

    private AuditLogEntity toEntity(AuditLog m) {
        AuditLogEntity e = new AuditLogEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setUserId(m.getUserId());
        e.setAction(m.getAction());
        e.setEntityType(m.getEntityType());
        e.setEntityId(m.getEntityId());
        e.setOldValueJson(m.getOldValueJson());
        e.setNewValueJson(m.getNewValueJson());
        e.setIpAddress(m.getIpAddress());
        e.setUserAgent(m.getUserAgent());
        e.setResult(m.getResult());
        e.setErrorDetail(m.getErrorDetail());
        e.setDurationMs(m.getDurationMs());
        return e;
    }

    private AuditLog toModel(AuditLogEntity e) {
        AuditLog m = new AuditLog();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setUserId(e.getUserId());
        m.setAction(e.getAction());
        m.setEntityType(e.getEntityType());
        m.setEntityId(e.getEntityId());
        m.setOldValueJson(e.getOldValueJson());
        m.setNewValueJson(e.getNewValueJson());
        m.setIpAddress(e.getIpAddress());
        m.setUserAgent(e.getUserAgent());
        m.setResult(e.getResult());
        m.setErrorDetail(e.getErrorDetail());
        m.setDurationMs(e.getDurationMs());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}
