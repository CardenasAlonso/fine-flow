package pe.edu.fineflow.support.application.usecase;

import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.application.port.in.ManageEventStoreUseCase;
import pe.edu.fineflow.support.domain.model.EventStoreEntry;
import pe.edu.fineflow.support.domain.port.out.EventStoreRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventStoreUseCaseImpl implements ManageEventStoreUseCase {

    private final EventStoreRepositoryPort repo;

    public EventStoreUseCaseImpl(EventStoreRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<EventStoreEntry> append(EventStoreEntry entry) {
        return TenantContext.getSchoolId()
                .flatMap(
                        sid -> {
                            if (entry.getId() == null) {
                                entry.setId(UuidGenerator.generate());
                            }
                            entry.setSchoolId(sid);
                            return repo.append(entry);
                        });
    }

    @Override
    public Flux<EventStoreEntry> findAggregateEvents(String aggregateType, String aggregateId) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findAggregateEvents(sid, aggregateType, aggregateId));
    }

    @Override
    public Flux<EventStoreEntry> findByCorrelationId(String correlationId) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findByCorrelationId(sid, correlationId));
    }
}