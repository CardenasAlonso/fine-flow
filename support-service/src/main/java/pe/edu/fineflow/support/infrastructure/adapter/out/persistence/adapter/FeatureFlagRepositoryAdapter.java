package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.support.domain.model.FeatureFlag;
import pe.edu.fineflow.support.domain.port.out.FeatureFlagRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.FeatureFlagEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.FeatureFlagR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FeatureFlagRepositoryAdapter implements FeatureFlagRepositoryPort {

    private final FeatureFlagR2dbcRepository repository;

    @Override
    public Mono<FeatureFlag> save(FeatureFlag flag) {
        return repository.save(toEntity(flag)).map(this::toModel);
    }

    @Override
    public Mono<FeatureFlag> findBySchoolAndFeature(String schoolId, String featureName) {
        return repository
                .findBySchoolIdAndFeatureName(schoolId, featureName)
                .map(this::toModel);
    }

    @Override
    public Flux<FeatureFlag> findBySchoolId(String schoolId) {
        return repository.findBySchoolId(schoolId).map(this::toModel);
    }

    private FeatureFlagEntity toEntity(FeatureFlag m) {
        FeatureFlagEntity e = new FeatureFlagEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setFeatureName(m.getFeatureName());
        e.setEnabled(m.getEnabled() != null && m.getEnabled() == 1 ? 1 : 0);
        e.setDescription(m.getDescription());
        e.setPlanRequired(m.getPlanRequired());
        e.setRolloutPct(m.getRolloutPct() != null ? m.getRolloutPct() : 100);
        e.setExpiresAt(m.getExpiresAt());
        e.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt() : Instant.now());
        return e;
    }

    private FeatureFlag toModel(FeatureFlagEntity e) {
        FeatureFlag m = new FeatureFlag();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setFeatureName(e.getFeatureName());
        m.setEnabled(e.getEnabled());
        m.setDescription(e.getDescription());
        m.setPlanRequired(e.getPlanRequired());
        m.setRolloutPct(e.getRolloutPct());
        m.setExpiresAt(e.getExpiresAt());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}