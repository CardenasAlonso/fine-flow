package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.SecurityEventEntity;
import reactor.core.publisher.Flux;

public interface SecurityEventR2dbcRepository extends ReactiveCrudRepository<SecurityEventEntity, String> {
    Flux<SecurityEventEntity> findBySchoolIdOrderByCreatedAtDesc(String schoolId);

    Flux<SecurityEventEntity> findBySeverityAndResolved(String severity, Integer resolved);

    Flux<SecurityEventEntity> findBySchoolIdAndEventTypeOrderByCreatedAtDesc(
            String schoolId, String eventType);
}