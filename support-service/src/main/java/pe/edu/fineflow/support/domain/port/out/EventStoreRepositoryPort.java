package pe.edu.fineflow.support.domain.port.out;

import pe.edu.fineflow.support.domain.model.EventStoreEntry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStoreRepositoryPort {
    Mono<EventStoreEntry> append(EventStoreEntry entry);

    Flux<EventStoreEntry> findAggregateEvents(String schoolId, String aggregateType, String aggregateId);

    Flux<EventStoreEntry> findByCorrelationId(String schoolId, String correlationId);
}