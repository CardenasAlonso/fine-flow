package pe.edu.fineflow.identity.application.usecase;

import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.AuthException;
import pe.edu.fineflow.common.security.JwtProvider;
import pe.edu.fineflow.common.security.UserPrincipal;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.identity.application.port.in.AuthUseCase;
import pe.edu.fineflow.identity.application.port.in.dto.AuthRequest;
import pe.edu.fineflow.identity.application.port.in.dto.AuthResponse;
import pe.edu.fineflow.identity.application.port.in.dto.RefreshTokenRequest;
import pe.edu.fineflow.identity.application.port.in.dto.RegisterRequest;
import pe.edu.fineflow.identity.domain.model.RefreshToken;
import pe.edu.fineflow.identity.domain.model.User;
import pe.edu.fineflow.identity.domain.port.out.RefreshTokenRepositoryPort;
import pe.edu.fineflow.identity.domain.port.out.SchoolDirectoryPort;
import pe.edu.fineflow.identity.domain.port.out.SecurityAuditPort;
import pe.edu.fineflow.identity.domain.port.out.UserRepositoryPort;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthUseCaseImpl.class);
    private static final Set<String> SELF_REGISTER_ROLES = Set.of("STUDENT", "GUARDIAN");

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final SchoolDirectoryPort schoolDirectoryPort;
    private final SecurityAuditPort securityAuditPort;
    private final LoginThrottleService throttle;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Mono<AuthResponse> login(AuthRequest request) {
        String throttleKey = request.schoolId() + ":" + request.email();
        return throttle.check(throttleKey)
                .then(Mono.defer(() -> userRepository.findByEmailAndSchoolId(request.email(), request.schoolId())))
                .switchIfEmpty(Mono.defer(() -> {
                    throttle.recordFailure(throttleKey);
                    recordAudit("LOGIN_FAILED", request.schoolId(), request.email());
                    return Mono.error(AuthException.invalidCredentials());
                }))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                        throttle.recordFailure(throttleKey);
                        recordAudit("LOGIN_FAILED", request.schoolId(), request.email());
                        return Mono.error(AuthException.invalidCredentials());
                    }
                    if (!user.isActive()) {
                        return Mono.error(AuthException.locked("Cuenta bloqueada o inactiva"));
                    }
                    throttle.reset(throttleKey);
                    user.recordLogin();
                    return userRepository.save(user);
                })
                .flatMap(user -> {
                    UserPrincipal principal = new UserPrincipal(
                            user.getId(), user.getSchoolId(), user.getEmail(), user.getRole(), null);
                    String accessToken = jwtProvider.generateAccessToken(principal);
                    String jti = UuidGenerator.generate();
                    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), jti);
                    RefreshToken rt = RefreshToken.create(
                            user.getId(), user.getSchoolId(),
                            jwtProvider.hashToken(refreshToken), jti,
                            Instant.now().plusMillis(jwtProvider.getRefreshTokenMs()));
                    return refreshTokenRepository.save(rt).thenReturn(new AuthResponse(
                            accessToken, refreshToken, "Bearer",
                            jwtProvider.getRefreshTokenMs() / 1000,
                            new AuthResponse.UserInfo(
                                    user.getId(), user.getEmail(), user.getRole(),
                                    user.getFirstName(), user.getLastName())));
                })
                .doOnSuccess(r -> recordAudit("LOGIN_SUCCESS", request.schoolId(), request.email()))
                .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, request.schoolId()));
    }

    @Override
    public Mono<AuthResponse> register(RegisterRequest request) {
        if (!SELF_REGISTER_ROLES.contains(request.role())) {
            recordAudit("ROLE_ESCALATION_ATTEMPT", request.schoolId(), request.email());
            return Mono.error(AuthException.forbidden("REGISTRATION_NOT_ALLOWED",
                    "Registro solo permitido para estudiantes y tutores. Contacte al administrador"));
        }
        return schoolDirectoryPort.existsById(request.schoolId())
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(AuthException.badRequest("SCHOOL_NOT_FOUND",
                        "La institución educativa no existe")))
                .then(Mono.defer(() -> userRepository.existsByEmailAndSchoolId(request.email(), request.schoolId())))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(AuthException.conflict(
                                "EMAIL_ALREADY_EXISTS",
                                "El correo ya está registrado en este colegio"));
                    }
                    User user = User.register(
                            request.email(),
                            passwordEncoder.encode(request.password()),
                            request.role(),
                            request.firstName(),
                            request.lastName());
                    user.setId(UuidGenerator.generate());
                    user.setSchoolId(request.schoolId());
                    user.setCreatedAt(Instant.now());
                    return userRepository.save(user);
                })
                .flatMap(user -> {
                    UserPrincipal principal = new UserPrincipal(
                            user.getId(), user.getSchoolId(), user.getEmail(), user.getRole(), null);
                    String accessToken = jwtProvider.generateAccessToken(principal);
                    String jti = UuidGenerator.generate();
                    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), jti);
                    RefreshToken rt = RefreshToken.create(
                            user.getId(), user.getSchoolId(),
                            jwtProvider.hashToken(refreshToken), jti,
                            Instant.now().plusMillis(jwtProvider.getRefreshTokenMs()));
                    return refreshTokenRepository.save(rt).thenReturn(new AuthResponse(
                            accessToken, refreshToken, "Bearer",
                            jwtProvider.getRefreshTokenMs() / 1000,
                            new AuthResponse.UserInfo(
                                    user.getId(), user.getEmail(), user.getRole(),
                                    user.getFirstName(), user.getLastName())));
                })
                .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, request.schoolId()));
    }

    @Override
    public Mono<AuthResponse> refresh(RefreshTokenRequest request) {
        String hashedToken = jwtProvider.hashToken(request.refreshToken());
        return refreshTokenRepository.findByTokenHash(hashedToken)
                .switchIfEmpty(Mono.error(AuthException.tokenInvalid()))
                .flatMap(storedToken -> {
                    if (storedToken.isRevoked()) {
                        return Mono.error(AuthException.tokenRevoked());
                    }
                    if (storedToken.isExpired()) {
                        return Mono.error(AuthException.tokenExpired());
                    }
                    return userRepository
                            .findByIdAndSchoolId(storedToken.getUserId(), storedToken.getSchoolId())
                            .switchIfEmpty(Mono.error(AuthException.userNotFound()))
                            .flatMap(user -> {
                                UserPrincipal principal = new UserPrincipal(
                                        user.getId(), user.getSchoolId(), user.getEmail(), user.getRole(), null);
                                String newAccessToken = jwtProvider.generateAccessToken(principal);
                                String newJti = UuidGenerator.generate();
                                String newRefreshToken = jwtProvider.generateRefreshToken(user.getId(), newJti);
                                RefreshToken newRt = storedToken.renew(
                                        jwtProvider.hashToken(newRefreshToken), newJti,
                                        Instant.now().plusMillis(jwtProvider.getRefreshTokenMs()));
                                return refreshTokenRepository.revokeByJtiAndSchoolId(
                                                storedToken.getJti(), storedToken.getSchoolId())
                                        .then(refreshTokenRepository.save(newRt))
                                        .thenReturn(new AuthResponse(
                                                newAccessToken, newRefreshToken, "Bearer",
                                                jwtProvider.getRefreshTokenMs() / 1000,
                                                new AuthResponse.UserInfo(
                                                        user.getId(), user.getEmail(), user.getRole(),
                                                        user.getFirstName(), user.getLastName())));
                            });
                })
                .doOnSuccess(r -> log.debug("Token refreshed successfully"));
    }

    @Override
    public Mono<Void> logout(RefreshTokenRequest request) {
        String hashedToken = jwtProvider.hashToken(request.refreshToken());
        return refreshTokenRepository.findByTokenHash(hashedToken)
                .flatMap(storedToken -> refreshTokenRepository.revokeByJtiAndSchoolId(
                        storedToken.getJti(), storedToken.getSchoolId()))
                .then();
    }

    @Override
    public Mono<Void> logoutAll(String userId, String schoolId) {
        return refreshTokenRepository.revokeAllByUserIdAndSchoolId(userId, schoolId);
    }

    private void recordAudit(String eventType, String schoolId, String email) {
        SecurityAuditPort.Event event = new SecurityAuditPort.Event(
                schoolId, null, eventType, "HIGH", "Auth event: " + eventType, null, null, null);
        securityAuditPort.recordAndForget(event);
    }
}