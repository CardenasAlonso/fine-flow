package pe.edu.fineflow.identity.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.fineflow.common.enums.ErrorCode;
import pe.edu.fineflow.common.exception.AuthException;
import pe.edu.fineflow.common.security.JwtProvider;
import pe.edu.fineflow.common.security.UserPrincipal;
import pe.edu.fineflow.identity.domain.model.RefreshToken;
import pe.edu.fineflow.identity.domain.model.User;
import pe.edu.fineflow.identity.domain.port.out.RefreshTokenRepositoryPort;
import pe.edu.fineflow.identity.domain.port.out.SchoolDirectoryPort;
import pe.edu.fineflow.identity.domain.port.out.SecurityAuditPort;
import pe.edu.fineflow.identity.domain.port.out.UserRepositoryPort;
import pe.edu.fineflow.identity.application.port.in.dto.AuthRequest;
import pe.edu.fineflow.identity.application.port.in.dto.AuthResponse;
import pe.edu.fineflow.identity.application.port.in.dto.RegisterRequest;
import pe.edu.fineflow.identity.application.port.in.dto.RefreshTokenRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AuthServiceTest {

    private UserRepositoryPort userRepository;
    private RefreshTokenRepositoryPort refreshTokenRepository;
    private SchoolDirectoryPort schoolDirectoryPort;
    private SecurityAuditPort securityAuditPort;
    private LoginThrottleService throttle;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private AuthUseCaseImpl authService;

    @BeforeEach
    void setUp() {
        userRepository = org.mockito.Mockito.mock(UserRepositoryPort.class);
        refreshTokenRepository = org.mockito.Mockito.mock(RefreshTokenRepositoryPort.class);
        schoolDirectoryPort = org.mockito.Mockito.mock(SchoolDirectoryPort.class);
        securityAuditPort = org.mockito.Mockito.mock(SecurityAuditPort.class);
        throttle = org.mockito.Mockito.mock(LoginThrottleService.class);
        passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        jwtProvider = org.mockito.Mockito.mock(JwtProvider.class);

        authService =
                new AuthUseCaseImpl(
                        userRepository,
                        refreshTokenRepository,
                        schoolDirectoryPort,
                        securityAuditPort,
                        throttle,
                        passwordEncoder,
                        jwtProvider);

        when(jwtProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any(), any())).thenReturn("refresh-token");
        when(jwtProvider.hashToken(any())).thenReturn("hashed-refresh");
        when(jwtProvider.getRefreshTokenMs()).thenReturn(7_200_000L);
        when(throttle.check(any())).thenReturn(Mono.empty());
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    private User activeUser(String email, String schoolId, String role) {
        User user = new User();
        user.setId("user-1");
        user.setSchoolId(schoolId);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash("encoded-hash");
        user.setFirstName("Ana");
        user.setLastName("Quispe");
        user.setStatus("ACTIVE");
        user.setCreatedAt(Instant.now());
        return user;
    }

    private AuthRequest loginRequest() {
        return new AuthRequest("ana@demo.edu.pe", "Passw0rd", "school-1");
    }

    private RegisterRequest registerRequest(String role) {
        return new RegisterRequest("ana@demo.edu.pe", "Passw0rd", "school-1", role, "Ana", "Quispe");
    }

    @Test
    void loginSuccessIssuesTokensAndResetsThrottle() {
        User user = activeUser("ana@demo.edu.pe", "school-1", "STUDENT");
        when(userRepository.findByEmailAndSchoolId("ana@demo.edu.pe", "school-1"))
                .thenReturn(Mono.just(user));
        when(userRepository.save(any())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("Passw0rd", "encoded-hash")).thenReturn(true);

        StepVerifier.create(authService.login(loginRequest()))
                .assertNext(
                        response -> {
                            org.assertj.core.api.Assertions.assertThat(response.accessToken())
                                    .isEqualTo("access-token");
                            org.assertj.core.api.Assertions.assertThat(response.refreshToken())
                                    .isEqualTo("refresh-token");
                            org.assertj.core.api.Assertions.assertThat(response.user().email())
                                    .isEqualTo("ana@demo.edu.pe");
                        })
                .verifyComplete();

        verify(throttle).reset("school-1:ana@demo.edu.pe");
        verify(refreshTokenRepository, times(1)).save(any());
        verify(securityAuditPort).recordAndForget(any());
    }

    @Test
    void loginWithWrongPasswordFailsAndRecordsFailure() {
        User user = activeUser("ana@demo.edu.pe", "school-1", "STUDENT");
        when(userRepository.findByEmailAndSchoolId("ana@demo.edu.pe", "school-1"))
                .thenReturn(Mono.just(user));
        when(passwordEncoder.matches("Passw0rd", "encoded-hash")).thenReturn(false);

        StepVerifier.create(authService.login(loginRequest()))
                .expectErrorMatches(
                        e -> e instanceof AuthException auth
                                && ErrorCode.INVALID_CREDENTIALS.getCode().equals(auth.getCode()))
                .verify();

        verify(throttle).recordFailure("school-1:ana@demo.edu.pe");
        verify(securityAuditPort).recordAndForget(any());
    }

    @Test
    void loginWithUnknownUserFails() {
        when(userRepository.findByEmailAndSchoolId(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(loginRequest()))
                .expectErrorMatches(
                        e -> e instanceof AuthException auth
                                && ErrorCode.INVALID_CREDENTIALS.getCode().equals(auth.getCode()))
                .verify();
    }

    @Test
    void loginWhenThrottledReturnsRateLimited() {
        when(throttle.check(any()))
                .thenReturn(Mono.error(AuthException.rateLimited()));

        StepVerifier.create(authService.login(loginRequest()))
                .expectErrorMatches(
                        e -> e instanceof AuthException auth
                                && ErrorCode.RATE_LIMITED.getCode().equals(auth.getCode()))
                .verify();

        verify(userRepository, never()).findByEmailAndSchoolId(any(), any());
    }

    @Test
    void registerRejectsUnauthorizedRole() {
        StepVerifier.create(authService.register(registerRequest("COORDINATOR")))
                .expectErrorMatches(
                        e -> e instanceof pe.edu.fineflow.common.exception.BusinessException be
                                && "REGISTRATION_NOT_ALLOWED".equals(be.getCode())
                                && org.springframework.http.HttpStatus.FORBIDDEN.equals(be.getStatus()))
                .verify();

        verify(securityAuditPort).recordAndForget(any());
    }

    @Test
    void registerRejectsUnknownSchool() {
        when(schoolDirectoryPort.existsById("school-1")).thenReturn(Mono.just(false));

        StepVerifier.create(authService.register(registerRequest("STUDENT")))
                .expectErrorMatches(
                        e -> e instanceof pe.edu.fineflow.common.exception.BusinessException be
                                && "SCHOOL_NOT_FOUND".equals(be.getCode()))
                .verify();
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(schoolDirectoryPort.existsById("school-1")).thenReturn(Mono.just(true));
        when(userRepository.existsByEmailAndSchoolId("ana@demo.edu.pe", "school-1"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(registerRequest("STUDENT")))
                .expectErrorMatches(
                        e -> e instanceof pe.edu.fineflow.common.exception.BusinessException be
                                && "EMAIL_ALREADY_EXISTS".equals(be.getCode()))
                .verify();
    }

    @Test
    void registerSuccessForStudent() {
        User user = activeUser("ana@demo.edu.pe", "school-1", "STUDENT");
        when(schoolDirectoryPort.existsById("school-1")).thenReturn(Mono.just(true));
        when(userRepository.existsByEmailAndSchoolId("ana@demo.edu.pe", "school-1"))
                .thenReturn(Mono.just(false));
        when(passwordEncoder.encode("Passw0rd")).thenReturn("encoded-hash");
        when(userRepository.save(any())).thenReturn(Mono.just(user));

        StepVerifier.create(authService.register(registerRequest("STUDENT")))
                .assertNext(
                        response -> {
                            org.assertj.core.api.Assertions.assertThat(response.accessToken())
                                    .isEqualTo("access-token");
                            org.assertj.core.api.Assertions.assertThat(response.user().role())
                                    .isEqualTo("STUDENT");
                        })
                .verifyComplete();

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void logoutRevokesToken() {
        when(jwtProvider.hashToken("refresh-token")).thenReturn("hashed-refresh");
        RefreshToken stored = RefreshToken.create(
                "user-1", "school-1", "hashed-refresh", "jti-1", Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByTokenHash("hashed-refresh"))
                .thenReturn(Mono.just(stored));
        when(refreshTokenRepository.revokeByJtiAndSchoolId("jti-1", "school-1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.logout(new RefreshTokenRequest("refresh-token")))
                .verifyComplete();

        verify(refreshTokenRepository).revokeByJtiAndSchoolId("jti-1", "school-1");
    }

    @Test
    void logoutAllRevokesAllUserTokens() {
        when(refreshTokenRepository.revokeAllByUserIdAndSchoolId("user-1", "school-1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.logoutAll("user-1", "school-1")).verifyComplete();
    }
}