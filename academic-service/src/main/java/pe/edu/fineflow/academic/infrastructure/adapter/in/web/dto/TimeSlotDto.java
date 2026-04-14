package pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TimeSlotDto(
        String id,
        Integer slotNumber,
        String slotName,
        String startTime,
        String endTime,
        Integer durationMin,
        String slotType,
        Integer isActive) {
    public record Create(
            @NotNull Integer slotNumber,
            @NotBlank String slotName,
            @NotBlank String startTime,
            @NotBlank String endTime,
            @NotNull Integer durationMin,
            @NotBlank String slotType) {}

    public record Update(
            @NotNull Integer slotNumber,
            @NotBlank String slotName,
            @NotBlank String startTime,
            @NotBlank String endTime,
            @NotNull Integer durationMin,
            @NotBlank String slotType,
            Integer isActive) {}
}
