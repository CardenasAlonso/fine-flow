package pe.edu.fineflow.identity.application.port.in.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserInfo user
) {
    public record UserInfo(
        String id,
        String email,
        String role,
        String firstName,
        String lastName
    ) {}
}