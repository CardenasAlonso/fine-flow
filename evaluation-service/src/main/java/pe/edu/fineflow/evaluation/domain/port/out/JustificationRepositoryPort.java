package pe.edu.fineflow.evaluation.domain.port.out;

import pe.edu.fineflow.evaluation.domain.model.Justification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JustificationRepositoryPort {
    Mono<Justification> save(Justification justification);

    Mono<Justification> findById(String id);

    Flux<Justification> findByStudent(String schoolId, String studentId);

    Flux<Justification> findByStatus(String schoolId, String status);
}