package pe.edu.fineflow.identity.application.port.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest (
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Formato de correo inválido")
    String email,

    @NotBlank(message = "La contraseña es requerida")
    String password,

    @NotBlank(message = "El ID del colegio es requerido")
    String schoolId
) {}