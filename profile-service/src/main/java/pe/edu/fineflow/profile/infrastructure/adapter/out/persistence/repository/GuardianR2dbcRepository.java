package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.GuardianEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GuardianR2dbcRepository extends ReactiveCrudRepository<GuardianEntity, String> {
    Mono<GuardianEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("SELECT * FROM GUARDIANS WHERE school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<GuardianEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    Flux<GuardianEntity> findAllByStudentId(String studentId);

    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);
}
