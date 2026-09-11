package pe.edu.fineflow.identity.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank
    String refreshToken
) {}