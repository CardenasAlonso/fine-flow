package pe.edu.fineflow.academic.application.port.in;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageTimeSlotUseCase {
    Mono<TimeSlot> create(TimeSlot timeSlot);

    Mono<TimeSlot> update(String id, TimeSlot timeSlot);

    Mono<Void> delete(String id);

    Mono<TimeSlot> findById(String id);

    Flux<TimeSlot> findAll(Pageable pageable);

    Flux<TimeSlot> findAllActive(Pageable pageable);
}
