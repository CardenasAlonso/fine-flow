package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository;

import java.time.Instant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RefreshTokenR2dbcRepository
        extends R2dbcRepository<RefreshTokenEntity, String> {
    Mono<RefreshTokenEntity> findByJtiAndSchoolId(String jti, String schoolId);

    Mono<RefreshTokenEntity> findByTokenHashAndSchoolId(String tokenHash, String schoolId);

    Mono<RefreshTokenEntity> findByTokenHash(String tokenHash);

    Flux<RefreshTokenEntity> findByUserIdAndSchoolIdAndRevokedAtIsNull(
            String userId, String schoolId);

    Flux<RefreshTokenEntity> findByExpiresAtBefore(Instant expiresAt);

    Mono<Void> deleteByExpiresAtBefore(Instant expiresAt);
}
