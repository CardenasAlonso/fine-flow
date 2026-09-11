package pe.edu.fineflow.support.application.port.in;

import pe.edu.fineflow.support.domain.model.FeatureFlag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageFeatureFlagUseCase {
    Mono<Boolean> isEnabled(String featureName);

    Mono<FeatureFlag> toggle(String featureName, boolean enabled);

    Mono<FeatureFlag> upsert(FeatureFlag flag);

    Flux<FeatureFlag> findAll();
}