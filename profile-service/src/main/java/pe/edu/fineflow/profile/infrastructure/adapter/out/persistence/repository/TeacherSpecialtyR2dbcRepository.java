package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.TeacherSpecialtyEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TeacherSpecialtyR2dbcRepository
        extends ReactiveCrudRepository<TeacherSpecialtyEntity, String> {
    Flux<TeacherSpecialtyEntity> findBySchoolIdAndTeacherId(String schoolId, String teacherId);

    Mono<TeacherSpecialtyEntity> findBySchoolIdAndTeacherIdAndSubject(
            String schoolId, String teacherId, String subject);
}