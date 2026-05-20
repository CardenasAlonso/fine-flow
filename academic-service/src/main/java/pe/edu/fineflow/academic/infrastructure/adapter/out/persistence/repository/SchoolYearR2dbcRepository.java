package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.SchoolYearEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SchoolYearR2dbcRepository
        extends ReactiveCrudRepository<SchoolYearEntity, String> {

    @Query("SELECT * FROM SCHOOL_YEARS WHERE id = :id AND school_id = :schoolId")
    Mono<SchoolYearEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("SELECT * FROM SCHOOL_YEARS WHERE school_id = :schoolId ORDER BY id OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<SchoolYearEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    @Query("DELETE FROM SCHOOL_YEARS WHERE id = :id AND school_id = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);

    Flux<SchoolYearEntity> findAllByAcademicLevelId(String academicLevelId);
}
