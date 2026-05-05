package pe.edu.fineflow.identity.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.fineflow.common.security.UserPrincipal;
import pe.edu.fineflow.identity.application.port.in.AuthUseCase;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.AuthRequest;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.AuthResponse;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.RefreshTokenRequest;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.RegisterRequest;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login y registro de usuarios")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y devuelve tokens JWT")
    public Mono<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return authUseCase.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authUseCase.register(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Refrescar token",
            description = "Genera nuevos tokens usando un refresh token válido")
    public Mono<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authUseCase.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cerrar sesión",
            description = "Revoca el refresh token proporcionado")
    public Mono<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        return authUseCase.logout(request);
    }

    @PostMapping("/logout-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cerrar todas las sesiones",
            description = "Revoca todos los refresh tokens del usuario autenticado")
    public Mono<Void> logoutAll(@AuthenticationPrincipal UserPrincipal principal) {
        return authUseCase.logoutAll(principal.userId(), principal.schoolId());
    }
}
