package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleVersionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScheduleVersionR2dbcRepository
        extends ReactiveCrudRepository<ScheduleVersionEntity, String> {
    Flux<ScheduleVersionEntity> findAllBySchoolId(String schoolId);

    Flux<ScheduleVersionEntity> findAllBySchoolIdAndStatus(String schoolId, String status);

    Flux<ScheduleVersionEntity> findAllBySchoolIdAndSchoolYearId(
            String schoolId, String schoolYearId);

    @Query("SELECT * FROM SCHEDULE_VERSIONS WHERE ID = :id AND SCHOOL_ID = :schoolId")
    Mono<ScheduleVersionEntity> findByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);

    @Query("SELECT * FROM SCHEDULE_VERSIONS WHERE school_id = :schoolId ORDER BY created_at DESC OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<ScheduleVersionEntity> findAllBySchoolId(@Param("schoolId") String schoolId, @Param("offset") int offset, @Param("limit") int limit);

    @Query("DELETE FROM SCHEDULE_VERSIONS WHERE ID = :id AND SCHOOL_ID = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);
}
