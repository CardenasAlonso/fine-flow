package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.AcademicLevelEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AcademicLevelR2dbcRepository
        extends ReactiveCrudRepository<AcademicLevelEntity, String> {

    @Query("SELECT * FROM ACADEMIC_LEVELS WHERE id = :id AND school_id = :schoolId")
    Mono<AcademicLevelEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("SELECT * FROM ACADEMIC_LEVELS WHERE school_id = :schoolId ORDER BY id OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AcademicLevelEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    @Query("DELETE FROM ACADEMIC_LEVELS WHERE id = :id AND school_id = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);
}
