package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.support.domain.model.FeatureCatalogItem;
import pe.edu.fineflow.support.domain.port.out.FeatureCatalogRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.FeatureCatalogEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.FeatureCatalogR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FeatureCatalogRepositoryAdapter implements FeatureCatalogRepositoryPort {

    private final FeatureCatalogR2dbcRepository repository;

    @Override
    public Flux<FeatureCatalogItem> findAll() {
        return repository.findAllByOrderBySortOrderAsc().map(this::toModel);
    }

    @Override
    public Mono<FeatureCatalogItem> findByKey(String featureKey) {
        return repository.findById(featureKey).map(this::toModel);
    }

    private FeatureCatalogItem toModel(FeatureCatalogEntity e) {
        return new FeatureCatalogItem(
                e.getFeatureKey(),
                e.getName(),
                e.getDescription(),
                e.getCategory(),
                e.getEnabledByDefault(),
                e.getMinPlan(),
                e.getIsCore(),
                e.getSortOrder());
    }
}