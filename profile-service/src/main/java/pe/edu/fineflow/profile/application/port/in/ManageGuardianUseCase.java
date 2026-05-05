package pe.edu.fineflow.profile.application.port.in;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.profile.domain.model.Guardian;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageGuardianUseCase {
    Mono<Guardian> create(Guardian guardian);

    Mono<Guardian> update(String id, Guardian guardian);

    Mono<Void> delete(String id);

    Mono<Guardian> findById(String id);

    Flux<Guardian> findAll(Pageable pageable);

    Flux<Guardian> findByStudent(String studentId);
}
