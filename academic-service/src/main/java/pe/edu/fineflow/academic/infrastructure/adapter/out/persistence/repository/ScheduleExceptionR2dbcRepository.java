package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleExceptionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScheduleExceptionR2dbcRepository
        extends ReactiveCrudRepository<ScheduleExceptionEntity, String> {
    Flux<ScheduleExceptionEntity> findAllBySchoolId(String schoolId);

    Flux<ScheduleExceptionEntity> findAllByClassScheduleId(String classScheduleId);

    Flux<ScheduleExceptionEntity> findAllBySchoolIdAndExceptionDate(
            String schoolId, java.time.LocalDate exceptionDate);

    @Query("SELECT * FROM SCHEDULE_EXCEPTIONS WHERE ID = :id AND SCHOOL_ID = :schoolId")
    Mono<ScheduleExceptionEntity> findByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);

    @Query("SELECT * FROM SCHEDULE_EXCEPTIONS WHERE school_id = :schoolId ORDER BY exception_date DESC OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<ScheduleExceptionEntity> findAllBySchoolId(@Param("schoolId") String schoolId, @Param("offset") int offset, @Param("limit") int limit);

    @Query("DELETE FROM SCHEDULE_EXCEPTIONS WHERE ID = :id AND SCHOOL_ID = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);
}
