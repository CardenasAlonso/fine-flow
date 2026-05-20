package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.academic.application.port.in.ManageScheduleExceptionUseCase;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto.ScheduleExceptionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/schedule-exceptions")
@RequiredArgsConstructor
@Tag(
        name = "Excepciones de Horario",
        description = "Gestión de excepciones al horario (suspensiones, cambios)")
public class ScheduleExceptionController {
    private final ManageScheduleExceptionUseCase useCase;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Obtener excepción por ID")
    public Mono<ScheduleExceptionDto> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toDto);
    }

    @GetMapping("/class-schedule/{classScheduleId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Listar excepciones por clase programada")
    public Flux<ScheduleExceptionDto> findByClassSchedule(@PathVariable String classScheduleId) {
        return useCase.findByClassSchedule(classScheduleId).map(this::toDto);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Listar excepciones por fecha")
    public Flux<ScheduleExceptionDto> findByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return useCase.findByDate(date).map(this::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva excepción")
    public Mono<ScheduleExceptionDto> create(
            @Valid @RequestBody ScheduleExceptionDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar excepción")
    public Mono<ScheduleExceptionDto> update(
            @PathVariable String id, @Valid @RequestBody ScheduleExceptionDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar excepción")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private ScheduleExceptionDto toDto(ScheduleException se) {
        return new ScheduleExceptionDto(
                se.getId(),
                se.getClassScheduleId(),
                se.getExceptionDate(),
                se.getExceptionType(),
                se.getSubstituteTeacherId(),
                se.getSubstituteClassroomId(),
                se.getSubstituteSlotId(),
                se.getReason(),
                se.getApprovedBy(),
                se.getApprovedAt(),
                se.getCreatedAt());
    }

    private ScheduleException toDomain(ScheduleExceptionDto.Create dto) {
        ScheduleException se = new ScheduleException();
        se.setClassScheduleId(dto.classScheduleId());
        se.setExceptionDate(dto.exceptionDate());
        se.setExceptionType(dto.exceptionType());
        se.setSubstituteTeacherId(dto.substituteTeacherId());
        se.setSubstituteClassroomId(dto.substituteClassroomId());
        se.setSubstituteSlotId(dto.substituteSlotId());
        se.setReason(dto.reason());
        return se;
    }

    private ScheduleException toDomainUpdate(ScheduleExceptionDto.Update dto) {
        ScheduleException se = new ScheduleException();
        se.setExceptionType(dto.exceptionType());
        se.setSubstituteTeacherId(dto.substituteTeacherId());
        se.setSubstituteClassroomId(dto.substituteClassroomId());
        se.setSubstituteSlotId(dto.substituteSlotId());
        se.setReason(dto.reason());
        return se;
    }
}
