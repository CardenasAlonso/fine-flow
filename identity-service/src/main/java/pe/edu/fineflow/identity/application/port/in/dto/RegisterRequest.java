package pe.edu.fineflow.identity.application.port.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Formato de correo inválido")
    @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    String email,

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "La contraseña debe incluir al menos una letra y un número"
    )
    String password,

    @NotBlank(message = "El ID del colegio es requerido")
    @Size(max = 50, message = "ID de colegio inválido")
    String schoolId,

    @NotBlank(message = "El rol es requerido")
    @Pattern(
        regexp = "STUDENT|GUARDIAN",
        message = "Rol no permitido para auto-registro. Contacte al administrador del colegio"
    )
    String role,

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String firstName,

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    String lastName
) {}