package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.SectionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SectionR2dbcRepository extends ReactiveCrudRepository<SectionEntity, String> {
    Flux<SectionEntity> findAllBySchoolId(String schoolId);

    Flux<SectionEntity> findAllBySchoolYearId(String schoolYearId);

    @Query("SELECT * FROM SECTIONS WHERE school_id = :schoolId AND is_active = 1")
    Flux<SectionEntity> findAllActiveBySchoolId(String schoolId);

    @Query("SELECT * FROM SECTIONS WHERE school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<SectionEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    @Query("SELECT * FROM SECTIONS WHERE school_id = :schoolId AND is_active = 1 OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<SectionEntity> findAllActiveBySchoolId(String schoolId, int offset, int limit);

    @Query("SELECT * FROM SECTIONS WHERE id = :id AND school_id = :schoolId")
    Mono<SectionEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("DELETE FROM SECTIONS WHERE id = :id AND school_id = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);
}
