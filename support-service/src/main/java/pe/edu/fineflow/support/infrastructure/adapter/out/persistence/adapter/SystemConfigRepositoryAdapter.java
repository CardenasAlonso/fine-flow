package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.support.domain.model.SystemConfig;
import pe.edu.fineflow.support.domain.port.out.SystemConfigRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.SystemConfigEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.SystemConfigR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SystemConfigRepositoryAdapter implements SystemConfigRepositoryPort {

    private final SystemConfigR2dbcRepository repository;

    @Override
    public Mono<SystemConfig> save(SystemConfig config) {
        return repository.save(toEntity(config)).map(this::toModel);
    }

    @Override
    public Mono<SystemConfig> findByKey(String schoolId, String configKey) {
        return repository.findBySchoolIdAndConfigKey(schoolId, configKey).map(this::toModel);
    }

    @Override
    public Flux<SystemConfig> findBySchoolId(String schoolId) {
        return repository.findBySchoolId(schoolId).map(this::toModel);
    }

    private SystemConfigEntity toEntity(SystemConfig m) {
        SystemConfigEntity e = new SystemConfigEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setConfigKey(m.getConfigKey());
        e.setConfigValue(m.getConfigValue());
        e.setValueType(m.getValueType());
        e.setDescription(m.getDescription());
        e.setIsSensitive(m.getIsSensitive());
        e.setUpdatedBy(m.getUpdatedBy());
        e.setUpdatedAt(m.getUpdatedAt());
        return e;
    }

    private SystemConfig toModel(SystemConfigEntity e) {
        SystemConfig m = new SystemConfig();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setConfigKey(e.getConfigKey());
        m.setConfigValue(e.getConfigValue());
        m.setValueType(e.getValueType());
        m.setDescription(e.getDescription());
        m.setIsSensitive(e.getIsSensitive());
        m.setUpdatedBy(e.getUpdatedBy());
        m.setUpdatedAt(e.getUpdatedAt());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}