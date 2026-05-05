package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.academic.application.port.in.ManageSectionUseCase;
import pe.edu.fineflow.academic.domain.model.Section;
import pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto.SectionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/sections")
@RequiredArgsConstructor
@Tag(name = "Secciones", description = "Gestión de aulas/secciones")
public class SectionController {
    private final ManageSectionUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar todas las secciones")
    public Flux<SectionDto.Response> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int offset = page * size;
        return useCase.findAll(offset, size).map(this::toResponse);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar secciones activas")
    public Flux<SectionDto.Response> findAllActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int offset = page * size;
        return useCase.findAllActive(offset, size).map(this::toResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Obtener sección por ID")
    public Mono<SectionDto.Response> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toResponse);
    }

    @GetMapping("/school-year/{schoolYearId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar secciones por año escolar")
    public Flux<SectionDto.Response> findBySchoolYear(@PathVariable String schoolYearId) {
        return useCase.findBySchoolYear(schoolYearId).map(this::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva sección")
    public Mono<SectionDto.Response> create(@Valid @RequestBody SectionDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar sección")
    public Mono<SectionDto.Response> update(
            @PathVariable String id, @Valid @RequestBody SectionDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar sección")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private SectionDto.Response toResponse(Section s) {
        return new SectionDto.Response(
                s.getId(),
                s.getName(),
                s.getMaxCapacity(),
                s.getTutorId(),
                s.getIsActive());
    }

    private Section toDomain(SectionDto.Create dto) {
        Section s = new Section();
        s.setName(dto.getName());
        s.setMaxCapacity(dto.getMaxCapacity());
        s.setSchoolYearId(dto.getSchoolYearId());
        return s;
    }

    private Section toDomainUpdate(SectionDto.Update dto) {
        Section s = new Section();
        s.setName(dto.getName());
        s.setMaxCapacity(dto.getMaxCapacity());
        s.setTutorId(dto.getTutorId());
        s.setIsActive(dto.getIsActive());
        return s;
    }
}
