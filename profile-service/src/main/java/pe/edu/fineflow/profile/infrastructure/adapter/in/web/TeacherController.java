package pe.edu.fineflow.profile.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.fineflow.profile.application.port.in.ManageTeacherUseCase;
import pe.edu.fineflow.profile.domain.model.Teacher;
import pe.edu.fineflow.profile.infrastructure.adapter.in.web.dto.TeacherDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/profile/teachers")
@RequiredArgsConstructor
@Tag(name = "Docentes", description = "Gestión de docentes")
public class TeacherController {

    private final ManageTeacherUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Listar todos los docentes")
    public Flux<TeacherDto.Response> findAll() {
        return useCase.findAll().map(this::toResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Obtener docente por ID")
    public Mono<TeacherDto.Response> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo docente")
    public Mono<TeacherDto.Response> create(@Valid @RequestBody TeacherDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar docente")
    public Mono<TeacherDto.Response> update(
            @PathVariable String id, @Valid @RequestBody TeacherDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar docente")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private TeacherDto.Response toResponse(Teacher t) {
        return new TeacherDto.Response(
                t.getId(),
                t.getFirstName(),
                t.getLastName(),
                t.getDocumentNumber(),
                t.getSpecialty(),
                t.getPhone(),
                t.getStatus(),
                t.getCreatedAt());
    }

    private Teacher toDomain(TeacherDto.Create dto) {
        Teacher t = new Teacher();
        t.setFirstName(dto.firstName());
        t.setLastName(dto.lastName());
        t.setDocumentNumber(dto.documentNumber());
        t.setSpecialty(dto.specialty());
        t.setPhone(dto.phone());
        return t;
    }

    private Teacher toDomainUpdate(TeacherDto.Update dto) {
        Teacher t = new Teacher();
        t.setFirstName(dto.firstName());
        t.setLastName(dto.lastName());
        t.setSpecialty(dto.specialty());
        t.setPhone(dto.phone());
        t.setStatus(dto.status());
        return t;
    }
}
