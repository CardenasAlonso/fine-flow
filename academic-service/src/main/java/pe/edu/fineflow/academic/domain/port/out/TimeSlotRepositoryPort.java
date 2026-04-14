package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.TimeSlot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TimeSlotRepositoryPort {
    Mono<TimeSlot> save(TimeSlot timeSlot);

    Mono<TimeSlot> findById(String id);

    Flux<TimeSlot> findAllBySchoolId(String schoolId);

    Flux<TimeSlot> findAllActiveBySchoolId(String schoolId);

    Mono<Void> deleteById(String id);
}
