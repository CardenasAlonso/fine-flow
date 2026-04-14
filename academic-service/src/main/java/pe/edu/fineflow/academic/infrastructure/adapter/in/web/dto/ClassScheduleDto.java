package pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClassScheduleDto(
        String id,
        String scheduleVersionId,
        String courseAssignmentId,
        String sectionId,
        String teacherId,
        String classroomId,
        String timeSlotId,
        Integer dayOfWeek,
        String weekType,
        String colorHex,
        String notes,
        Integer isActive,
        Instant createdAt) {
    public record Create(
            @NotBlank String scheduleVersionId,
            @NotBlank String courseAssignmentId,
            @NotBlank String sectionId,
            @NotBlank String teacherId,
            @NotBlank String classroomId,
            @NotBlank String timeSlotId,
            @NotNull Integer dayOfWeek,
            String weekType,
            String colorHex,
            String notes) {}

    public record Update(
            @NotBlank String classroomId,
            @NotBlank String timeSlotId,
            @NotNull Integer dayOfWeek,
            String weekType,
            String colorHex,
            String notes) {}
}
