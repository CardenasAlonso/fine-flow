package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.academic.application.port.in.ManageClassroomUseCase;
import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto.ClassroomDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/classrooms")
@RequiredArgsConstructor
@Tag(name = "Aulas", description = "Gestión de aulas y espacios físicos")
public class ClassroomController {
    private final ManageClassroomUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar todas las aulas")
    public Flux<ClassroomDto> findAll() {
        return useCase.findAll().map(this::toDto);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar aulas activas")
    public Flux<ClassroomDto> findAllActive() {
        return useCase.findAllActive().map(this::toDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Obtener aula por ID")
    public Mono<ClassroomDto> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva aula")
    public Mono<ClassroomDto> create(@Valid @RequestBody ClassroomDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar aula")
    public Mono<ClassroomDto> update(
            @PathVariable String id, @Valid @RequestBody ClassroomDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar aula")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private ClassroomDto toDto(Classroom c) {
        return new ClassroomDto(
                c.getId(),
                c.getName(),
                c.getRoomType(),
                c.getCapacity(),
                c.getFloorNumber(),
                c.getBuilding(),
                c.getHasProjector(),
                c.getHasComputers(),
                c.getIsActive(),
                c.getNotes(),
                c.getCreatedAt());
    }

    private Classroom toDomain(ClassroomDto.Create dto) {
        Classroom c = new Classroom();
        c.setName(dto.name());
        c.setRoomType(dto.roomType());
        c.setCapacity(dto.capacity());
        c.setFloorNumber(dto.floorNumber());
        c.setBuilding(dto.building());
        c.setHasProjector(dto.hasProjector() != null ? dto.hasProjector() : 0);
        c.setHasComputers(dto.hasComputers() != null ? dto.hasComputers() : 0);
        c.setNotes(dto.notes());
        return c;
    }

    private Classroom toDomainUpdate(ClassroomDto.Update dto) {
        Classroom c = new Classroom();
        c.setName(dto.name());
        c.setRoomType(dto.roomType());
        c.setCapacity(dto.capacity());
        c.setFloorNumber(dto.floorNumber());
        c.setBuilding(dto.building());
        c.setHasProjector(dto.hasProjector() != null ? dto.hasProjector() : 0);
        c.setHasComputers(dto.hasComputers() != null ? dto.hasComputers() : 0);
        c.setIsActive(dto.isActive());
        c.setNotes(dto.notes());
        return c;
    }
}
