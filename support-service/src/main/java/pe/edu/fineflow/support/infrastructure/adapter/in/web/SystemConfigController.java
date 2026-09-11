package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.support.application.port.in.ManageSystemConfigUseCase;
import pe.edu.fineflow.support.domain.model.SystemConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support/config")
@Tag(name = "Configuración por Colegio", description = "SYSTEM_CONFIG por tenant")
public class SystemConfigController {

    private final ManageSystemConfigUseCase useCase;

    public SystemConfigController(ManageSystemConfigUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<SystemConfig> findAll() {
        return useCase.findAll();
    }

    @GetMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<SystemConfig> findByKey(@PathVariable String key) {
        return useCase.findByKey(key);
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<SystemConfig> upsert(
            @PathVariable String key, @RequestBody SystemConfig body) {
        return useCase.upsert(
                key, body.getConfigValue(), body.getValueType(), body.getDescription());
    }
}