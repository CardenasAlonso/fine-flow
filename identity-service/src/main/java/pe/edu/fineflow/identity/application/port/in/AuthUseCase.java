package pe.edu.fineflow.identity.application.port.in;

import pe.edu.fineflow.identity.application.port.in.dto.AuthRequest;
import pe.edu.fineflow.identity.application.port.in.dto.AuthResponse;
import pe.edu.fineflow.identity.application.port.in.dto.RefreshTokenRequest;
import pe.edu.fineflow.identity.application.port.in.dto.RegisterRequest;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<AuthResponse> login(AuthRequest request);

    Mono<AuthResponse> register(RegisterRequest request);

    Mono<AuthResponse> refresh(RefreshTokenRequest request);

    Mono<Void> logout(RefreshTokenRequest request);

    Mono<Void> logoutAll(String userId, String schoolId);
}
