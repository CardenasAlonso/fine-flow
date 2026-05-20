package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.CourseAssignmentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CourseAssignmentR2dbcRepository
        extends ReactiveCrudRepository<CourseAssignmentEntity, String> {
    @Query("SELECT * FROM COURSE_ASSIGNMENTS WHERE id = :id AND school_id = :schoolId")
    Mono<CourseAssignmentEntity> findByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);

    @Query("SELECT * FROM COURSE_ASSIGNMENTS WHERE school_id = :schoolId ORDER BY created_at DESC OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<CourseAssignmentEntity> findAllBySchoolId(@Param("schoolId") String schoolId, @Param("offset") int offset, @Param("limit") int limit);

    @Query("DELETE FROM COURSE_ASSIGNMENTS WHERE id = :id AND school_id = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);

    Flux<CourseAssignmentEntity> findAllBySectionId(String sectionId);
}
