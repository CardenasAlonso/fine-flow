package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserR2dbcRepository extends ReactiveCrudRepository<UserEntity, String> {
    Mono<UserEntity> findByEmailAndSchoolId(String email, String schoolId);

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmailAndSchoolId(String email, String schoolId);

    Flux<UserEntity> findAllBySchoolId(String schoolId);

    @Query(
            "SELECT * FROM USERS WHERE ID = :id AND SCHOOL_ID = :schoolId AND IS_DELETED = 0")
    Mono<UserEntity> findByIdAndSchoolId(String id, String schoolId);
}
