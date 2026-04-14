package pe.edu.fineflow.academic.application.port.in;

import pe.edu.fineflow.academic.domain.model.Classroom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageClassroomUseCase {
    Mono<Classroom> create(Classroom classroom);

    Mono<Classroom> update(String id, Classroom classroom);

    Mono<Void> delete(String id);

    Mono<Classroom> findById(String id);

    Flux<Classroom> findAll();

    Flux<Classroom> findAllActive();
}
