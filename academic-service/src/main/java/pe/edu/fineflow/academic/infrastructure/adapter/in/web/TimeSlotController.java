package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.academic.application.port.in.ManageTimeSlotUseCase;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto.TimeSlotDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/time-slots")
@RequiredArgsConstructor
@Tag(name = "Franjas Horarias", description = "Gestión de franjas horarias del día")
public class TimeSlotController {
    private final ManageTimeSlotUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Listar todas las franjas horarias")
    public Flux<TimeSlotDto> findAll() {
        return useCase.findAll().map(this::toDto);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar franjas horarias activas")
    public Flux<TimeSlotDto> findAllActive() {
        return useCase.findAllActive().map(this::toDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Obtener franja horaria por ID")
    public Mono<TimeSlotDto> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva franja horaria")
    public Mono<TimeSlotDto> create(@Valid @RequestBody TimeSlotDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar franja horaria")
    public Mono<TimeSlotDto> update(
            @PathVariable String id, @Valid @RequestBody TimeSlotDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar franja horaria")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private TimeSlotDto toDto(TimeSlot t) {
        return new TimeSlotDto(
                t.getId(),
                t.getSlotNumber(),
                t.getSlotName(),
                t.getStartTime(),
                t.getEndTime(),
                t.getDurationMin(),
                t.getSlotType(),
                t.getIsActive());
    }

    private TimeSlot toDomain(TimeSlotDto.Create dto) {
        TimeSlot t = new TimeSlot();
        t.setSlotNumber(dto.slotNumber());
        t.setSlotName(dto.slotName());
        t.setStartTime(dto.startTime());
        t.setEndTime(dto.endTime());
        t.setDurationMin(dto.durationMin());
        t.setSlotType(dto.slotType());
        return t;
    }

    private TimeSlot toDomainUpdate(TimeSlotDto.Update dto) {
        TimeSlot t = new TimeSlot();
        t.setSlotNumber(dto.slotNumber());
        t.setSlotName(dto.slotName());
        t.setStartTime(dto.startTime());
        t.setEndTime(dto.endTime());
        t.setDurationMin(dto.durationMin());
        t.setSlotType(dto.slotType());
        t.setIsActive(dto.isActive());
        return t;
    }
}
