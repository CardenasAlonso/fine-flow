package pe.edu.fineflow.evaluation.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.evaluation.application.port.in.ManageJustificationUseCase;
import pe.edu.fineflow.evaluation.domain.model.Justification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/evaluation/justifications")
@Tag(name = "Justificaciones", description = "Flujo de justificación de inasistencias")
public class JustificationController {

    private final ManageJustificationUseCase useCase;

    public JustificationController(ManageJustificationUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','GUARDIAN')")
    public Mono<Justification> request(@RequestBody Justification justification) {
        return useCase.request(justification);
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<Justification> review(
            @PathVariable String id,
            @RequestBody ReviewRequest request) {
        return useCase.review(id, request.decision(), request.reviewNote());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<Justification> findByStudent(@PathVariable String studentId) {
        return useCase.findByStudent(studentId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<Justification> findByStatus(
            @RequestParam(defaultValue = "PENDING") String status) {
        return useCase.findByStatus(status);
    }

    public record ReviewRequest(String decision, String reviewNote) {}
}