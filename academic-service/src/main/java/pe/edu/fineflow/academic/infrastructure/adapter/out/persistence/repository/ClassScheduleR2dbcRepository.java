package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassScheduleEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ClassScheduleR2dbcRepository
        extends ReactiveCrudRepository<ClassScheduleEntity, String> {
    Flux<ClassScheduleEntity> findAllBySchoolId(String schoolId);

    Flux<ClassScheduleEntity> findAllByScheduleVersionId(String scheduleVersionId);

    Flux<ClassScheduleEntity> findAllByScheduleVersionIdAndDayOfWeek(
            String scheduleVersionId, Integer dayOfWeek);

    Flux<ClassScheduleEntity> findAllByTeacherId(String teacherId);

    Flux<ClassScheduleEntity> findAllBySectionId(String sectionId);

    Flux<ClassScheduleEntity> findAllByClassroomId(String classroomId);
}
