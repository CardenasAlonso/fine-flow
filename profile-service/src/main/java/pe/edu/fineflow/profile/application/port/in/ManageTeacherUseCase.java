package pe.edu.fineflow.profile.application.port.in;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.profile.domain.model.Teacher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageTeacherUseCase {
    Mono<Teacher> create(Teacher teacher);

    Mono<Teacher> update(String id, Teacher teacher);

    Mono<Void> delete(String id);

    Mono<Teacher> findById(String id);

    Flux<Teacher> findAll(Pageable pageable);
}
