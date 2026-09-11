package pe.edu.fineflow.support.application.port.in;

import pe.edu.fineflow.support.domain.model.EventStoreEntry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageEventStoreUseCase {
    Mono<EventStoreEntry> append(EventStoreEntry entry);

    Flux<EventStoreEntry> findAggregateEvents(String aggregateType, String aggregateId);

    Flux<EventStoreEntry> findByCorrelationId(String correlationId);
}