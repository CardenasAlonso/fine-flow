package pe.edu.fineflow.profile.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.profile.application.port.in.ManageTeacherSpecialtyUseCase;
import pe.edu.fineflow.profile.domain.model.TeacherSpecialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/profile/teachers/{teacherId}/specialties")
@Tag(name = "Especialidades del Docente", description = "TEACHER_SPECIALTIES por docente")
public class TeacherSpecialtyController {

    private final ManageTeacherSpecialtyUseCase useCase;

    public TeacherSpecialtyController(ManageTeacherSpecialtyUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<TeacherSpecialty> findByTeacher(@PathVariable String teacherId) {
        return useCase.findByTeacher(teacherId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<TeacherSpecialty> add(
            @PathVariable String teacherId, @RequestBody TeacherSpecialty specialty) {
        specialty.setTeacherId(teacherId);
        return useCase.add(specialty);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<Void> remove(@PathVariable String teacherId, @PathVariable String id) {
        return useCase.remove(id);
    }
}