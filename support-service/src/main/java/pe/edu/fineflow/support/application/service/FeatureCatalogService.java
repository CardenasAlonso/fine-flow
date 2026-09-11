package pe.edu.fineflow.support.application.service;

import org.springframework.stereotype.Service;
import pe.edu.fineflow.support.application.port.in.ManageFeatureCatalogUseCase;
import pe.edu.fineflow.support.domain.model.FeatureCatalogItem;
import pe.edu.fineflow.support.domain.port.out.FeatureCatalogRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeatureCatalogService implements ManageFeatureCatalogUseCase {

    private final FeatureCatalogRepositoryPort repo;

    public FeatureCatalogService(FeatureCatalogRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Flux<FeatureCatalogItem> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<FeatureCatalogItem> findByKey(String featureKey) {
        return repo.findByKey(featureKey);
    }
}