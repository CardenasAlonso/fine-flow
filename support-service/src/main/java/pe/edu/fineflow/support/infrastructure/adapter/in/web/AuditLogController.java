package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.support.application.port.in.ManageAuditLogUseCase;
import pe.edu.fineflow.support.domain.model.AuditLog;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/support/audit-logs")
@Tag(name = "Auditoría", description = "Registro de auditoría del sistema")
public class AuditLogController {

    private final ManageAuditLogUseCase useCase;

    public AuditLogController(ManageAuditLogUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<AuditLog> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.findAll(pageable);
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<AuditLog> findByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.findByAction(action, pageable);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<AuditLog> findByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.findByUserId(userId, pageable);
    }
}
