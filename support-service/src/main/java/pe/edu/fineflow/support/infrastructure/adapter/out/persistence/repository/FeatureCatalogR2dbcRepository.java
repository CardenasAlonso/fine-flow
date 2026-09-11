package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.FeatureCatalogEntity;
import reactor.core.publisher.Flux;

public interface FeatureCatalogR2dbcRepository
        extends ReactiveCrudRepository<FeatureCatalogEntity, String> {
    Flux<FeatureCatalogEntity> findAllByOrderBySortOrderAsc();
}