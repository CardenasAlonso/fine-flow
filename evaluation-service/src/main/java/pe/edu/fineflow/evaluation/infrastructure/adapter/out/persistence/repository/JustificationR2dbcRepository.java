package pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.entity.JustificationEntity;
import reactor.core.publisher.Flux;

public interface JustificationR2dbcRepository extends ReactiveCrudRepository<JustificationEntity, String> {
    Flux<JustificationEntity> findBySchoolIdAndStudentIdOrderByRequestedAtDesc(
            String schoolId, String studentId);

    Flux<JustificationEntity> findBySchoolIdAndStatusOrderByRequestedAtAsc(
            String schoolId, String status);
}