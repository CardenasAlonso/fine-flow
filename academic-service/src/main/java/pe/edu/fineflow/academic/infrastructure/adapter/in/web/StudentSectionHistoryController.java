package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.academic.application.port.in.ManageStudentSectionHistoryUseCase;
import pe.edu.fineflow.academic.domain.model.StudentSectionHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/section-history")
@Tag(name = "Historial de Secciones", description = "Trazabilidad del cambio de sección del estudiante")
public class StudentSectionHistoryController {

    private final ManageStudentSectionHistoryUseCase useCase;

    public StudentSectionHistoryController(ManageStudentSectionHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<StudentSectionHistory> record(@RequestBody StudentSectionHistory history) {
        return useCase.record(history);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<StudentSectionHistory> findByStudent(@PathVariable String studentId) {
        return useCase.findByStudent(studentId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<StudentSectionHistory> findBySchool() {
        return useCase.findBySchool();
    }
}