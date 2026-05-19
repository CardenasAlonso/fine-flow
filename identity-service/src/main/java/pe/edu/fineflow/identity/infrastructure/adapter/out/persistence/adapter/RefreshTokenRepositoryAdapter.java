package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.identity.domain.model.RefreshToken;
import pe.edu.fineflow.identity.domain.port.out.RefreshTokenRepositoryPort;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository.RefreshTokenR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {
    private final RefreshTokenR2dbcRepository repository;

    @Override
    public Mono<RefreshToken> save(RefreshToken token) {
        RefreshTokenEntity entity = toEntity(token);
        return repository.save(entity).map(this::toModel);
    }

    @Override
    public Mono<RefreshToken> findByJtiAndSchoolId(String jti, String schoolId) {
        return repository.findByJtiAndSchoolId(jti, schoolId).map(this::toModel);
    }

    @Override
    public Mono<RefreshToken> findByTokenHashAndSchoolId(String tokenHash, String schoolId) {
        return repository.findByTokenHashAndSchoolId(tokenHash, schoolId).map(this::toModel);
    }

    @Override
    public Mono<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(this::toModel);
    }

    @Override
    public Flux<RefreshToken> findActiveByUserIdAndSchoolId(String userId, String schoolId) {
        return repository
                .findByUserIdAndSchoolIdAndRevokedAtIsNull(userId, schoolId)
                .map(this::toModel);
    }

    @Override
    public Mono<Void> revokeByJtiAndSchoolId(String jti, String schoolId) {
        return repository
                .findByJtiAndSchoolId(jti, schoolId)
                .flatMap(
                        e -> {
                            e.setRevokedAt(Instant.now());
                            return repository.save(e);
                        })
                .then();
    }

    @Override
    public Mono<Void> revokeAllByUserIdAndSchoolId(String userId, String schoolId) {
        return repository
                .findByUserIdAndSchoolIdAndRevokedAtIsNull(userId, schoolId)
                .flatMap(
                        e -> {
                            e.setRevokedAt(Instant.now());
                            return repository.save(e);
                        })
                .then();
    }

    @Override
    public Mono<Long> deleteExpiredTokens() {
        return repository
                .findByExpiresAtBefore(Instant.now())
                .count()
                .flatMap(
                        count -> {
                            if (count > 0) {
                                return repository
                                        .deleteByExpiresAtBefore(Instant.now())
                                        .then(Mono.just(count));
                            }
                            return Mono.just(0L);
                        });
    }

    private RefreshTokenEntity toEntity(RefreshToken m) {
        RefreshTokenEntity e = new RefreshTokenEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setUserId(m.getUserId());
        e.setTokenHash(m.getTokenHash());
        e.setJti(m.getJti());
        e.setDeviceInfo(m.getDeviceInfo());
        e.setIpAddress(m.getIpAddress());
        e.setRevokedAt(m.getRevokedAt());
        e.setRevokeReason(m.getRevokeReason());
        e.setExpiresAt(m.getExpiresAt());
        e.setCreatedAt(m.getCreatedAt());
        return e;
    }

    private RefreshToken toModel(RefreshTokenEntity e) {
        return new RefreshToken(
                e.getId(),
                e.getSchoolId(),
                e.getCreatedAt(),
                e.getUserId(),
                e.getTokenHash(),
                e.getJti(),
                e.getDeviceInfo(),
                e.getIpAddress(),
                e.getRevokedAt(),
                e.getRevokeReason(),
                e.getExpiresAt());
    }
}
