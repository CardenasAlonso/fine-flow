package pe.edu.fineflow.profile.domain.port.out;

import pe.edu.fineflow.profile.domain.model.TeacherSpecialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TeacherSpecialtyRepositoryPort {
    Mono<TeacherSpecialty> save(TeacherSpecialty specialty);

    Mono<Void> delete(String id);

    Flux<TeacherSpecialty> findByTeacher(String schoolId, String teacherId);
}