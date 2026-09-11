package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.FeatureFlagEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeatureFlagR2dbcRepository extends ReactiveCrudRepository<FeatureFlagEntity, String> {
    Flux<FeatureFlagEntity> findBySchoolId(String schoolId);

    Mono<FeatureFlagEntity> findBySchoolIdAndFeatureName(String schoolId, String featureName);
}