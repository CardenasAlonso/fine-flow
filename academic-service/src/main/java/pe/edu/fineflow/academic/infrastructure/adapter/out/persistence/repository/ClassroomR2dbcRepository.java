package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassroomEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ClassroomR2dbcRepository extends ReactiveCrudRepository<ClassroomEntity, String> {
    Flux<ClassroomEntity> findAllBySchoolId(String schoolId);

    Flux<ClassroomEntity> findAllBySchoolIdAndIsActive(String schoolId, Integer isActive);
}
