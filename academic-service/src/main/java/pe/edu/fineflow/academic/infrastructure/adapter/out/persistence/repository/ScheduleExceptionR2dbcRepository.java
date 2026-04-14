package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleExceptionEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ScheduleExceptionR2dbcRepository
        extends ReactiveCrudRepository<ScheduleExceptionEntity, String> {
    Flux<ScheduleExceptionEntity> findAllBySchoolIdAndIsActive(String schoolId, Integer isActive);

    Flux<ScheduleExceptionEntity> findAllByClassScheduleIdAndIsActive(
            String classScheduleId, Integer isActive);

    Flux<ScheduleExceptionEntity> findAllBySchoolIdAndExceptionDateAndIsActive(
            String schoolId, java.time.LocalDate exceptionDate, Integer isActive);
}
