package pe.edu.fineflow.academic.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.academic.application.port.in.ManageClassScheduleUseCase;
import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto.ClassScheduleDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/academic/class-schedules")
@RequiredArgsConstructor
@Tag(name = "Clases Programadas", description = "Gestión de clases programadas en el horario")
public class ClassScheduleController {
    private final ManageClassScheduleUseCase useCase;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Obtener clase programada por ID")
    public Mono<ClassScheduleDto> findById(@PathVariable String id) {
        return useCase.findById(id).map(this::toDto);
    }

    @GetMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    @Operation(summary = "Listar clases por versión de horario")
    public Flux<ClassScheduleDto> findByScheduleVersion(@PathVariable String scheduleVersionId) {
        return useCase.findByScheduleVersion(scheduleVersionId).map(this::toDto);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar clases por docente")
    public Flux<ClassScheduleDto> findByTeacher(@PathVariable String teacherId) {
        return useCase.findByTeacher(teacherId).map(this::toDto);
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR','TEACHER')")
    @Operation(summary = "Listar clases por sección")
    public Flux<ClassScheduleDto> findBySection(@PathVariable String sectionId) {
        return useCase.findBySection(sectionId).map(this::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva clase programada")
    public Mono<ClassScheduleDto> create(@Valid @RequestBody ClassScheduleDto.Create request) {
        return useCase.create(toDomain(request)).map(this::toDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar clase programada")
    public Mono<ClassScheduleDto> update(
            @PathVariable String id, @Valid @RequestBody ClassScheduleDto.Update request) {
        return useCase.update(id, toDomainUpdate(request)).map(this::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar clase programada")
    public Mono<Void> delete(@PathVariable String id) {
        return useCase.delete(id);
    }

    private ClassScheduleDto toDto(ClassSchedule cs) {
        return new ClassScheduleDto(
                cs.getId(),
                cs.getScheduleVersionId(),
                cs.getCourseAssignmentId(),
                cs.getSectionId(),
                cs.getTeacherId(),
                cs.getClassroomId(),
                cs.getTimeSlotId(),
                cs.getDayOfWeek(),
                cs.getWeekType(),
                cs.getColorHex(),
                cs.getNotes(),
                cs.getIsActive(),
                cs.getCreatedAt());
    }

    private ClassSchedule toDomain(ClassScheduleDto.Create dto) {
        ClassSchedule cs = new ClassSchedule();
        cs.setScheduleVersionId(dto.scheduleVersionId());
        cs.setCourseAssignmentId(dto.courseAssignmentId());
        cs.setSectionId(dto.sectionId());
        cs.setTeacherId(dto.teacherId());
        cs.setClassroomId(dto.classroomId());
        cs.setTimeSlotId(dto.timeSlotId());
        cs.setDayOfWeek(dto.dayOfWeek());
        cs.setWeekType(dto.weekType() != null ? dto.weekType() : "ALL");
        cs.setColorHex(dto.colorHex());
        cs.setNotes(dto.notes());
        return cs;
    }

    private ClassSchedule toDomainUpdate(ClassScheduleDto.Update dto) {
        ClassSchedule cs = new ClassSchedule();
        cs.setClassroomId(dto.classroomId());
        cs.setTimeSlotId(dto.timeSlotId());
        cs.setDayOfWeek(dto.dayOfWeek());
        cs.setWeekType(dto.weekType() != null ? dto.weekType() : "ALL");
        cs.setColorHex(dto.colorHex());
        cs.setNotes(dto.notes());
        return cs;
    }
}
