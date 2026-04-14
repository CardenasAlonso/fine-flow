package pe.edu.fineflow.profile.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class TeacherDto {
    private TeacherDto() {}

    public record Create(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            @NotBlank @Size(min = 8, max = 20) String documentNumber,
            String specialty,
            String phone) {}

    public record Update(
            @NotBlank String firstName,
            @NotBlank String lastName,
            String specialty,
            String phone,
            String status) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(
            String id,
            String firstName,
            String lastName,
            String documentNumber,
            String specialty,
            String phone,
            String status,
            Instant createdAt) {}
}
