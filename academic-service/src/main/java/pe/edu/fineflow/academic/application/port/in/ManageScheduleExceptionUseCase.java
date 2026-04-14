package pe.edu.fineflow.academic.application.port.in;

import java.time.LocalDate;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageScheduleExceptionUseCase {
    Mono<ScheduleException> create(ScheduleException exception);

    Mono<ScheduleException> update(String id, ScheduleException exception);

    Mono<Void> delete(String id);

    Mono<ScheduleException> findById(String id);

    Flux<ScheduleException> findByClassSchedule(String classScheduleId);

    Flux<ScheduleException> findByDate(LocalDate date);
}
