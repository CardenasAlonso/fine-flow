package pe.edu.fineflow.support.application.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.application.port.in.ManageFeatureFlagUseCase;
import pe.edu.fineflow.support.domain.model.FeatureFlag;
import pe.edu.fineflow.support.domain.port.out.FeatureFlagRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeatureFlagService implements ManageFeatureFlagUseCase {

    private final FeatureFlagRepositoryPort repo;

    public FeatureFlagService(FeatureFlagRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Boolean> isEnabled(String featureName) {
        return TenantContext.getSchoolId()
                .flatMap(sid -> repo.findBySchoolAndFeature(sid, featureName))
                .map(flag -> flag.getEnabled() != null && flag.getEnabled() == 1)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<FeatureFlag> toggle(String featureName, boolean enabled) {
        return TenantContext.getSchoolId()
                .flatMap(
                        sid -> repo.findBySchoolAndFeature(sid, featureName)
                                .flatMap(
                                        flag -> {
                                            flag.setEnabled(enabled ? 1 : 0);
                                            return repo.save(flag);
                                        }));
    }

    @Override
    public Mono<FeatureFlag> upsert(FeatureFlag flag) {
        return TenantContext.getPrincipal()
                .flatMap(
                        p -> repo.findBySchoolAndFeature(p.schoolId(), flag.getFeatureName())
                                .flatMap(
                                        existing -> {
                                            existing.setEnabled(
                                                    flag.getEnabled() != null ? flag.getEnabled() : 0);
                                            existing.setDescription(flag.getDescription());
                                            existing.setPlanRequired(flag.getPlanRequired());
                                            existing.setRolloutPct(
                                                    flag.getRolloutPct() != null
                                                            ? flag.getRolloutPct()
                                                            : 100);
                                            existing.setExpiresAt(flag.getExpiresAt());
                                            return repo.save(existing);
                                        })
                                .switchIfEmpty(
                                        Mono.defer(() -> {
                                            flag.setId(UuidGenerator.generate());
                                            flag.setSchoolId(p.schoolId());
                                            flag.setEnabled(
                                                    flag.getEnabled() != null ? flag.getEnabled() : 0);
                                            flag.setRolloutPct(
                                                    flag.getRolloutPct() != null
                                                            ? flag.getRolloutPct()
                                                            : 100);
                                            flag.setCreatedAt(Instant.now());
                                            return repo.save(flag);
                                        })));
    }

    @Override
    public Flux<FeatureFlag> findAll() {
        return TenantContext.getSchoolId().flatMapMany(sid -> repo.findBySchoolId(sid));
    }
}