package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.academic.application.port.in.ManageScheduleVersionUseCase;
import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto.ScheduleVersionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/schedule-versions")
@RequiredArgsConstructor
@Tag(name = "Versiones de Horario", description = "Gestión de versiones del horario escolar")
public class ScheduleVersionController {
    private final ManageScheduleVersionUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Listar todas las versiones de horario")
    public Flux<ScheduleVersionDto> findAll() {
        return useCase.findAll().map(this::toDto);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Obtener horario activo")
    public Flux<ScheduleVersionDto> findActive() {
        return useCase.findActive().map(this::toDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Obtener versión de horario por ID")
    public Mono<ScheduleVersionDto> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva versión de horario")
    public Mono<ScheduleVersionDto> create(@Valid @RequestBody ScheduleVersionDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar versión de horario")
    public Mono<ScheduleVersionDto> update(
            @PathVariable String id, @Valid @RequestBody ScheduleVersionDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toDto);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publicar versión de horario")
    public Mono<ScheduleVersionDto> publish(@PathVariable String id) {
        return useCase.publish(id).map(this::toDto);
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Archivar versión de horario")
    public Mono<ScheduleVersionDto> archive(@PathVariable String id) {
        return useCase.archive(id).map(this::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar versión de horario")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private ScheduleVersionDto toDto(ScheduleVersion sv) {
        return new ScheduleVersionDto(
                sv.getId(),
                sv.getSchoolYearId(),
                sv.getAcademicPeriodId(),
                sv.getVersionName(),
                sv.getStatus(),
                sv.getNotes(),
                sv.getCreatedBy(),
                sv.getApprovedBy(),
                sv.getApprovedAt(),
                sv.getPublishedAt(),
                sv.getValidFrom(),
                sv.getValidUntil(),
                sv.getCreatedAt(),
                sv.getUpdatedAt());
    }

    private ScheduleVersion toDomain(ScheduleVersionDto.Create dto) {
        ScheduleVersion sv = new ScheduleVersion();
        sv.setVersionName(dto.versionName());
        sv.setSchoolYearId(dto.schoolYearId());
        sv.setAcademicPeriodId(dto.academicPeriodId());
        sv.setNotes(dto.notes());
        sv.setValidFrom(dto.validFrom());
        sv.setValidUntil(dto.validUntil());
        return sv;
    }

    private ScheduleVersion toDomainUpdate(ScheduleVersionDto.Update dto) {
        ScheduleVersion sv = new ScheduleVersion();
        sv.setVersionName(dto.versionName());
        sv.setAcademicPeriodId(dto.academicPeriodId());
        sv.setNotes(dto.notes());
        sv.setValidFrom(dto.validFrom());
        sv.setValidUntil(dto.validUntil());
        return sv;
    }
}
