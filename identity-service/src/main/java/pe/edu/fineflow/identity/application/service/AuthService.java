package pe.edu.fineflow.identity.application.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.AuthException;
import pe.edu.fineflow.common.security.JwtProvider;
import pe.edu.fineflow.common.security.UserPrincipal;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.identity.application.port.in.AuthUseCase;
import pe.edu.fineflow.identity.domain.model.RefreshToken;
import pe.edu.fineflow.identity.domain.model.User;
import pe.edu.fineflow.identity.domain.port.out.RefreshTokenRepositoryPort;
import pe.edu.fineflow.identity.domain.port.out.UserRepositoryPort;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.AuthRequest;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.AuthResponse;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.RefreshTokenRequest;
import pe.edu.fineflow.identity.infrastructure.adapter.in.web.dto.RegisterRequest;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Mono<AuthResponse> login(AuthRequest request) {
        return userRepository
                .findByEmailAndSchoolId(request.getEmail(), request.getSchoolId())
                .switchIfEmpty(Mono.error(AuthException.invalidCredentials()))
                .flatMap(
                        user -> {
                            if (!passwordEncoder.matches(
                                    request.getPassword(), user.getPasswordHash())) {
                                return Mono.error(AuthException.invalidCredentials());
                            }
                            if (!"ACTIVE".equals(user.getStatus())) {
                                return Mono.error(
                                        AuthException.locked("Cuenta bloqueada o inactiva"));
                            }
                            user.setLastLoginAt(Instant.now());
                            return userRepository.save(user);
                        })
                .flatMap(
                        user -> {
                            UserPrincipal principal =
                                    new UserPrincipal(
                                            user.getId(),
                                            user.getSchoolId(),
                                            user.getEmail(),
                                            user.getRole(),
                                            null);
                             String accessToken = jwtProvider.generateAccessToken(principal);
                             String jti = UuidGenerator.generate();
                              String refreshToken =
                                      jwtProvider.generateRefreshToken(user.getId(), jti);
                              RefreshToken rt = new RefreshToken(
                                      jti,
                                      user.getSchoolId(),
                                      Instant.now(),
                                      user.getId(),
                                      jwtProvider.hashToken(refreshToken),
                                      jti,
                                      null,
                                      null,
                                      null,
                                      null,
                                      Instant.now().plusMillis(jwtProvider.getRefreshTokenMs()));
                              return refreshTokenRepository
                                      .save(rt)
                                      .thenReturn(
                                              new AuthResponse(
                                                      accessToken,
                                                      refreshToken,
                                                      "Bearer",
                                                      jwtProvider.getRefreshTokenMs() / 1000,
                                                      new AuthResponse.UserInfo(
                                                              user.getId(),
                                                              user.getEmail(),
                                                              user.getRole(),
                                                              user.getFirstName(),
                                                              user.getLastName())));
                          });
     }

     @Override
     public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository
                .existsByEmailAndSchoolId(request.getEmail(), request.getSchoolId())
                .flatMap(
                        exists -> {
                            if (exists) {
                                return Mono.error(
                                        AuthException.conflict(
                                                "EMAIL_ALREADY_EXISTS",
                                                "El correo ya está registrado en este colegio"));
                            }
                            User user = new User();
                            user.setId(UuidGenerator.generate());
                            user.setSchoolId(request.getSchoolId());
                            user.setEmail(request.getEmail());
                            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                            user.setRole(request.getRole());
                            user.setFirstName(request.getFirstName());
                            user.setLastName(request.getLastName());
                            user.setStatus("ACTIVE");
                            user.setCreatedAt(Instant.now());
                            return userRepository.save(user);
                        })
                .flatMap(
                        user -> {
                            UserPrincipal principal =
                                    new UserPrincipal(
                                            user.getId(),
                                            user.getSchoolId(),
                                            user.getEmail(),
                                            user.getRole(),
                                            null);
                            String accessToken = jwtProvider.generateAccessToken(principal);
                            String jti = UuidGenerator.generate();
                             String refreshToken =
                                      jwtProvider.generateRefreshToken(user.getId(), jti);
                              RefreshToken rt = new RefreshToken(
                                      jti,
                                      user.getSchoolId(),
                                      Instant.now(),
                                      user.getId(),
                                      jwtProvider.hashToken(refreshToken),
                                      jti,
                                      null,
                                      null,
                                      null,
                                      null,
                                      Instant.now().plusMillis(jwtProvider.getRefreshTokenMs()));
                              return refreshTokenRepository
                                      .save(rt)
                                      .thenReturn(
                                              new AuthResponse(
                                                      accessToken,
                                                      refreshToken,
                                                      "Bearer",
                                                      jwtProvider.getRefreshTokenMs() / 1000,
                                                      new AuthResponse.UserInfo(
                                                              user.getId(),
                                                              user.getEmail(),
                                                              user.getRole(),
                                                              user.getFirstName(),
                                                              user.getLastName())));
                          });
     }

     @Override
     public Mono<AuthResponse> refresh(RefreshTokenRequest request) {
        String hashedToken = jwtProvider.hashToken(request.getRefreshToken());
        return refreshTokenRepository
                .findByTokenHash(hashedToken)
                .switchIfEmpty(Mono.error(AuthException.tokenInvalid()))
                .flatMap(
                        storedToken -> {
                             if (storedToken.getRevokedAt() != null) {
                                 return Mono.error(AuthException.tokenRevoked());
                             }
                            if (storedToken.getExpiresAt().isBefore(Instant.now())) {
                                return Mono.error(AuthException.tokenExpired());
                            }
                            return userRepository
                                    .findByIdAndSchoolId(
                                            storedToken.getUserId(),
                                            storedToken.getSchoolId())
                                    .switchIfEmpty(Mono.error(AuthException.userNotFound()))
                                    .flatMap(
                                            user -> {
                                                UserPrincipal principal =
                                                        new UserPrincipal(
                                                                user.getId(),
                                                                user.getSchoolId(),
                                                                user.getEmail(),
                                                                user.getRole(),
                                                                null);
                                                String newAccessToken =
                                                        jwtProvider.generateAccessToken(principal);
                                                String newJti = UuidGenerator.generate();
                                                String newRefreshToken =
                                                        jwtProvider.generateRefreshToken(
                                                                user.getId(), newJti);
                                                  RefreshToken newRt = new RefreshToken(
                                                          newJti,
                                                          user.getSchoolId(),
                                                          Instant.now(),
                                                          user.getId(),
                                                          jwtProvider.hashToken(newRefreshToken),
                                                          newJti,
                                                          storedToken.getDeviceInfo(),
                                                          storedToken.getIpAddress(),
                                                          null,
                                                          null,
                                                          Instant.now()
                                                                  .plusMillis(
                                                                          jwtProvider
                                                                                  .getRefreshTokenMs()));
                                                return refreshTokenRepository
                                                        .revokeByJtiAndSchoolId(
                                                                storedToken.getJti(),
                                                                storedToken.getSchoolId())
                                                        .then(refreshTokenRepository.save(newRt))
                                                        .thenReturn(
                                                                new AuthResponse(
                                                                        newAccessToken,
                                                                        newRefreshToken,
                                                                        "Bearer",
                                                                        jwtProvider
                                                                                        .getRefreshTokenMs()
                                                                                / 1000,
                                                                        new AuthResponse.UserInfo(
                                                                                user.getId(),
                                                                                user.getEmail(),
                                                                                user.getRole(),
                                                                                user.getFirstName(),
                                                                                user.getLastName())));
                                            });
                        });
    }

    @Override
    public Mono<Void> logout(RefreshTokenRequest request) {
        String hashedToken = jwtProvider.hashToken(request.getRefreshToken());
        return refreshTokenRepository
                .findByTokenHash(hashedToken)
                .flatMap(
                        storedToken ->
                                refreshTokenRepository.revokeByJtiAndSchoolId(
                                        storedToken.getJti(), storedToken.getSchoolId()))
                .then();
    }

    @Override
    public Mono<Void> logoutAll(String userId, String schoolId) {
        return refreshTokenRepository.revokeAllByUserIdAndSchoolId(userId, schoolId);
    }
}
