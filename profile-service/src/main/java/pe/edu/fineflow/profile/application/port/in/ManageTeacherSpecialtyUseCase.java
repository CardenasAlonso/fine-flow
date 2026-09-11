package pe.edu.fineflow.profile.application.port.in;

import pe.edu.fineflow.profile.domain.model.TeacherSpecialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageTeacherSpecialtyUseCase {
    Mono<TeacherSpecialty> add(TeacherSpecialty specialty);

    Mono<Void> remove(String id);

    Flux<TeacherSpecialty> findByTeacher(String teacherId);
}