package pe.edu.fineflow.academic.application.port.in;

import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageScheduleVersionUseCase {
    Mono<ScheduleVersion> create(ScheduleVersion scheduleVersion);

    Mono<ScheduleVersion> update(String id, ScheduleVersion scheduleVersion);

    Mono<ScheduleVersion> publish(String id);

    Mono<ScheduleVersion> archive(String id);

    Mono<Void> delete(String id);

    Mono<ScheduleVersion> findById(String id);

    Flux<ScheduleVersion> findAll();

    Flux<ScheduleVersion> findActive();
}
