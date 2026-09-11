package pe.edu.fineflow.support.application.usecase;

import org.springframework.stereotype.Service;
import pe.edu.fineflow.support.application.port.in.ManageFeatureCatalogUseCase;
import pe.edu.fineflow.support.domain.model.FeatureCatalogItem;
import pe.edu.fineflow.support.domain.port.out.FeatureCatalogRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeatureCatalogUseCaseImpl implements ManageFeatureCatalogUseCase {

    private final FeatureCatalogRepositoryPort repo;

    public FeatureCatalogUseCaseImpl(FeatureCatalogRepositoryPort repo) {
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