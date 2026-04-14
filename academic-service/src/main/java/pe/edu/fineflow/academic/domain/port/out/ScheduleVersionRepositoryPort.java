package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScheduleVersionRepositoryPort {
    Mono<ScheduleVersion> save(ScheduleVersion scheduleVersion);

    Mono<ScheduleVersion> findById(String id);

    Flux<ScheduleVersion> findAllBySchoolId(String schoolId);

    Flux<ScheduleVersion> findActiveBySchoolId(String schoolId);

    Mono<Void> deleteById(String id);
}
