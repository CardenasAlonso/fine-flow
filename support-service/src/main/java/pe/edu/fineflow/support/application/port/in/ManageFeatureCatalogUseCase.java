package pe.edu.fineflow.support.application.port.in;

import pe.edu.fineflow.support.domain.model.FeatureCatalogItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageFeatureCatalogUseCase {
    Flux<FeatureCatalogItem> findAll();

    Mono<FeatureCatalogItem> findByKey(String featureKey);
}