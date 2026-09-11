package pe.edu.fineflow.evaluation.application.port.in;

import pe.edu.fineflow.evaluation.domain.model.Justification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageJustificationUseCase {
    Mono<Justification> request(Justification justification);

    Mono<Justification> review(String id, String decision, String reviewNote);

    Flux<Justification> findByStudent(String studentId);

    Flux<Justification> findByStatus(String status);
}