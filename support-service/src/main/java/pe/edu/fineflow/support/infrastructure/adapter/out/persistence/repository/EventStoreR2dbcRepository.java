package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.EventStoreEntity;
import reactor.core.publisher.Flux;

public interface EventStoreR2dbcRepository extends ReactiveCrudRepository<EventStoreEntity, String> {
    Flux<EventStoreEntity> findBySchoolIdAndAggregateTypeAndAggregateIdOrderByEventVersionAsc(
            String schoolId, String aggregateType, String aggregateId);

    Flux<EventStoreEntity> findBySchoolIdAndCorrelationId(String schoolId, String correlationId);
}