package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.Classroom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClassroomRepositoryPort {
    Mono<Classroom> save(Classroom classroom);

    Mono<Classroom> findById(String id);

    Flux<Classroom> findAllBySchoolId(String schoolId);

    Flux<Classroom> findAllActiveBySchoolId(String schoolId);

    Mono<Void> deleteById(String id);
}
