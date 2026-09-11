package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.support.application.port.in.ManageEventStoreUseCase;
import pe.edu.fineflow.support.domain.model.EventStoreEntry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support/events")
@Tag(name = "Event Store", description = "Base para Event Sourcing")
public class EventStoreController {

    private final ManageEventStoreUseCase useCase;

    public EventStoreController(ManageEventStoreUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Mono<EventStoreEntry> append(@RequestBody EventStoreEntry entry) {
        return useCase.append(entry);
    }

    @GetMapping("/aggregates/{aggregateType}/{aggregateId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<EventStoreEntry> aggregateEvents(
            @PathVariable String aggregateType, @PathVariable String aggregateId) {
        return useCase.findAggregateEvents(aggregateType, aggregateId);
    }

    @GetMapping("/correlation/{correlationId}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINATOR')")
    public Flux<EventStoreEntry> byCorrelation(@PathVariable String correlationId) {
        return useCase.findByCorrelationId(correlationId);
    }
}