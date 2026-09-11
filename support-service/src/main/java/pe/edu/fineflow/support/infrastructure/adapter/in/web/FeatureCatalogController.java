package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.support.application.port.in.ManageFeatureCatalogUseCase;
import pe.edu.fineflow.support.domain.model.FeatureCatalogItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support/feature-catalog")
@Tag(name = "Catálogo de Features", description = "Catálogo maestro de módulos K12")
public class FeatureCatalogController {

    private final ManageFeatureCatalogUseCase useCase;

    public FeatureCatalogController(ManageFeatureCatalogUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<FeatureCatalogItem> findAll() {
        return useCase.findAll();
    }

    @GetMapping("/{featureKey}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<FeatureCatalogItem> findByKey(@PathVariable String featureKey) {
        return useCase.findByKey(featureKey);
    }
}