package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.support.application.port.in.ManageSecurityEventUseCase;
import pe.edu.fineflow.support.domain.model.SecurityEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support/security-events")
@Tag(name = "Eventos de Seguridad", description = "Log de eventos de seguridad")
public class SecurityEventController {

    private final ManageSecurityEventUseCase useCase;

    public SecurityEventController(ManageSecurityEventUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<SecurityEvent> log(@RequestBody SecurityEvent event) {
        return useCase.log(event);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<SecurityEvent> findMySchool() {
        return useCase.findByMySchool();
    }

    @GetMapping("/unresolved")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Flux<SecurityEvent> unresolved(
            @RequestParam(defaultValue = "CRITICAL") String severity) {
        return useCase.findUnresolvedBySeverity(severity);
    }

    @PatchMapping("/{id}/resolved")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<Void> markResolved(@PathVariable String id) {
        return useCase.markResolved(id);
    }
}