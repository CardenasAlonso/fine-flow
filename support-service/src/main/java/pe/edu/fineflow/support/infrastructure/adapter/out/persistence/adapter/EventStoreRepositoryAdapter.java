package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.support.domain.model.EventStoreEntry;
import pe.edu.fineflow.support.domain.port.out.EventStoreRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.EventStoreEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.EventStoreR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EventStoreRepositoryAdapter implements EventStoreRepositoryPort {

    private final EventStoreR2dbcRepository repository;

    @Override
    public Mono<EventStoreEntry> append(EventStoreEntry entry) {
        return repository.save(toEntity(entry)).map(this::toModel);
    }

    @Override
    public Flux<EventStoreEntry> findAggregateEvents(
            String schoolId, String aggregateType, String aggregateId) {
        return repository
                .findBySchoolIdAndAggregateTypeAndAggregateIdOrderByEventVersionAsc(
                        schoolId, aggregateType, aggregateId)
                .map(this::toModel);
    }

    @Override
    public Flux<EventStoreEntry> findByCorrelationId(String schoolId, String correlationId) {
        return repository
                .findBySchoolIdAndCorrelationId(schoolId, correlationId)
                .map(this::toModel);
    }

    private EventStoreEntity toEntity(EventStoreEntry m) {
        EventStoreEntity e = new EventStoreEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setAggregateType(m.getAggregateType());
        e.setAggregateId(m.getAggregateId());
        e.setEventType(m.getEventType());
        e.setEventVersion(m.getEventVersion());
        e.setPayloadJson(m.getPayloadJson());
        e.setMetadataJson(m.getMetadataJson());
        e.setCausationId(m.getCausationId());
        e.setCorrelationId(m.getCorrelationId());
        e.setOccurredAt(m.getOccurredAt() != null ? m.getOccurredAt() : Instant.now());
        return e;
    }

    private EventStoreEntry toModel(EventStoreEntity e) {
        EventStoreEntry m = new EventStoreEntry();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setAggregateType(e.getAggregateType());
        m.setAggregateId(e.getAggregateId());
        m.setEventType(e.getEventType());
        m.setEventVersion(e.getEventVersion());
        m.setPayloadJson(e.getPayloadJson());
        m.setMetadataJson(e.getMetadataJson());
        m.setCausationId(e.getCausationId());
        m.setCorrelationId(e.getCorrelationId());
        m.setOccurredAt(e.getOccurredAt());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}