package pe.edu.fineflow.support.domain.port.out;

import pe.edu.fineflow.support.domain.model.FeatureCatalogItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeatureCatalogRepositoryPort {
    Flux<FeatureCatalogItem> findAll();

    Mono<FeatureCatalogItem> findByKey(String featureKey);
}