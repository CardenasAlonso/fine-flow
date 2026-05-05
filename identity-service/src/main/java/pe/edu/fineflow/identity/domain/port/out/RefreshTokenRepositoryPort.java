package pe.edu.fineflow.identity.domain.port.out;

import pe.edu.fineflow.identity.domain.model.RefreshToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepositoryPort {
    Mono<RefreshToken> save(RefreshToken token);

    Mono<RefreshToken> findByJtiAndSchoolId(String jti, String schoolId);

    Mono<RefreshToken> findByTokenHashAndSchoolId(String tokenHash, String schoolId);

    Flux<RefreshToken> findActiveByUserIdAndSchoolId(String userId, String schoolId);

    Mono<Void> revokeByJtiAndSchoolId(String jti, String schoolId);

    Mono<Void> revokeAllByUserIdAndSchoolId(String userId, String schoolId);

    Mono<Long> deleteExpiredTokens();
}
