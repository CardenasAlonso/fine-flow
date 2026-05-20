package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassroomEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClassroomR2dbcRepository extends ReactiveCrudRepository<ClassroomEntity, String> {

    @Query("SELECT * FROM CLASSROOMS WHERE id = :id AND school_id = :schoolId")
    Mono<ClassroomEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("SELECT * FROM CLASSROOMS WHERE school_id = :schoolId ORDER BY id OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<ClassroomEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    @Query("DELETE FROM CLASSROOMS WHERE id = :id AND school_id = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);

    Flux<ClassroomEntity> findAllBySchoolIdAndIsActive(String schoolId, Integer isActive);
}
