package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassTaskEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClassTaskR2dbcRepository extends R2dbcRepository<ClassTaskEntity, String> {
    Mono<ClassTaskEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("SELECT * FROM CLASS_TASKS WHERE school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<ClassTaskEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    Flux<ClassTaskEntity> findAllByCourseAssignmentId(String courseAssignmentId);

    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);
}
