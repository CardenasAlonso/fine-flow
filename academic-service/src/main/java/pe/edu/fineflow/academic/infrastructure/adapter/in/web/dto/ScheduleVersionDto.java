package pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScheduleVersionDto(
        String id,
        String schoolYearId,
        String academicPeriodId,
        String versionName,
        String status,
        String notes,
        String createdBy,
        String approvedBy,
        Instant approvedAt,
        Instant publishedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        Instant createdAt,
        Instant updatedAt) {
    public record Create(
            @NotBlank String versionName,
            @NotBlank String schoolYearId,
            String academicPeriodId,
            String notes,
            LocalDate validFrom,
            LocalDate validUntil) {}

    public record Update(
            @NotBlank String versionName,
            String academicPeriodId,
            String notes,
            LocalDate validFrom,
            LocalDate validUntil) {}
}
