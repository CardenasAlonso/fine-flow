package pe.edu.fineflow.support.domain.port.out;

import pe.edu.fineflow.support.domain.model.FeatureFlag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeatureFlagRepositoryPort {
    Mono<FeatureFlag> save(FeatureFlag flag);

    Mono<FeatureFlag> findBySchoolAndFeature(String schoolId, String featureName);

    Flux<FeatureFlag> findBySchoolId(String schoolId);
}