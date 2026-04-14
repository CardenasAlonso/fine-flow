package pe.edu.fineflow.academic.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClassroomDto(
        String id,
        String name,
        String roomType,
        Integer capacity,
        Integer floorNumber,
        String building,
        Integer hasProjector,
        Integer hasComputers,
        Integer isActive,
        String notes,
        Instant createdAt) {
    public record Create(
            @NotBlank String name,
            @NotBlank String roomType,
            @NotNull Integer capacity,
            Integer floorNumber,
            String building,
            Integer hasProjector,
            Integer hasComputers,
            String notes) {}

    public record Update(
            @NotBlank String name,
            @NotBlank String roomType,
            @NotNull Integer capacity,
            Integer floorNumber,
            String building,
            Integer hasProjector,
            Integer hasComputers,
            Integer isActive,
            String notes) {}
}
