package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.support.application.port.in.ManageFeatureFlagUseCase;
import pe.edu.fineflow.support.domain.model.FeatureFlag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support/feature-flags")
@Tag(name = "Feature Flags", description = "Control de funcionalidades por colegio")
public class FeatureFlagController {

    private final ManageFeatureFlagUseCase useCase;

    public FeatureFlagController(ManageFeatureFlagUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<FeatureFlag> findAll() {
        return useCase.findAll();
    }

    @GetMapping("/{featureName}/enabled")
    @PreAuthorize("isAuthenticated()")
    public Mono<Boolean> isEnabled(@PathVariable String featureName) {
        return useCase.isEnabled(featureName);
    }

    @PostMapping("/{featureName}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<FeatureFlag> toggle(
            @PathVariable String featureName, @RequestBody ToggleRequest request) {
        return useCase.toggle(featureName, request.enabled());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<FeatureFlag> upsert(@RequestBody FeatureFlag flag) {
        return useCase.upsert(flag);
    }

    public record ToggleRequest(boolean enabled) {}
}