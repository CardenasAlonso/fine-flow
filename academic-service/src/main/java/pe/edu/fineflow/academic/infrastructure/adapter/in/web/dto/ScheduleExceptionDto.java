package pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScheduleExceptionDto(
        String id,
        String classScheduleId,
        LocalDate exceptionDate,
        String exceptionType,
        String substituteTeacherId,
        String substituteClassroomId,
        String substituteSlotId,
        String reason,
        String approvedBy,
        Instant approvedAt,
        Instant createdAt) {
    public record Create(
            @NotBlank String classScheduleId,
            @NotNull LocalDate exceptionDate,
            @NotBlank String exceptionType,
            String substituteTeacherId,
            String substituteClassroomId,
            String substituteSlotId,
            @NotBlank String reason) {}

    public record Update(
            @NotBlank String exceptionType,
            String substituteTeacherId,
            String substituteClassroomId,
            String substituteSlotId,
            @NotBlank String reason) {}
}
