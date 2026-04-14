package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ScheduleException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScheduleExceptionRepositoryPort {
    Mono<ScheduleException> save(ScheduleException exception);

    Mono<ScheduleException> findById(String id);

    Flux<ScheduleException> findAllByClassScheduleId(String classScheduleId);

    Flux<ScheduleException> findAllByDate(String schoolId, java.time.LocalDate date);

    Mono<Void> deleteById(String id);
}
