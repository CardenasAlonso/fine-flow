package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassScheduleEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Query("SELECT * FROM CLASS_SCHEDULES WHERE ID = :id AND SCHOOL_ID = :schoolId")
    Mono<ClassScheduleEntity> findByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);

    @Query("DELETE FROM CLASS_SCHEDULES WHERE ID = :id AND SCHOOL_ID = :schoolId")
    Mono<Void> deleteByIdAndSchoolId(@Param("id") String id, @Param("schoolId") String schoolId);
}
