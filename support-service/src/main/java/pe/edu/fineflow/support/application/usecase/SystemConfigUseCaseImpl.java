package pe.edu.fineflow.support.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.application.port.in.ManageSystemConfigUseCase;
import pe.edu.fineflow.support.domain.model.SystemConfig;
import pe.edu.fineflow.support.domain.port.out.SystemConfigRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SystemConfigUseCaseImpl implements ManageSystemConfigUseCase {

    private final SystemConfigRepositoryPort repo;

    public SystemConfigUseCaseImpl(SystemConfigRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<SystemConfig> upsert(String key, String value, String valueType, String description) {
        return TenantContext.getPrincipal()
                .flatMap(
                        p -> repo.findByKey(p.schoolId(), key)
                                .flatMap(
                                        existing -> {
                                            existing.setConfigValue(value);
                                            existing.setValueType(valueType);
                                            existing.setDescription(description);
                                            existing.setUpdatedBy(p.userId());
                                            existing.setUpdatedAt(Instant.now());
                                            return repo.save(existing);
                                        })
                                .switchIfEmpty(
                                        Mono.defer(() -> {
                                            SystemConfig cfg = new SystemConfig();
                                            cfg.setId(UuidGenerator.generate());
                                            cfg.setSchoolId(p.schoolId());
                                            cfg.setConfigKey(key);
                                            cfg.setConfigValue(value);
                                            cfg.setValueType(valueType);
                                            cfg.setDescription(description);
                                            cfg.setIsSensitive(0);
                                            cfg.setUpdatedBy(p.userId());
                                            cfg.setUpdatedAt(Instant.now());
                                            cfg.setCreatedAt(Instant.now());
                                            return repo.save(cfg);
                                        })));
    }

    @Override
    public Mono<SystemConfig> findByKey(String key) {
        return TenantContext.getSchoolId().flatMap(sid -> repo.findByKey(sid, key));
    }

    @Override
    public Flux<SystemConfig> findAll() {
        return TenantContext.getSchoolId().flatMapMany(sid -> repo.findBySchoolId(sid));
    }
}