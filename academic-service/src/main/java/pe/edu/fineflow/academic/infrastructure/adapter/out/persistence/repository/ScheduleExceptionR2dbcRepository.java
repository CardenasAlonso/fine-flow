package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleExceptionEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ScheduleExceptionR2dbcRepository
        extends ReactiveCrudRepository<ScheduleExceptionEntity, String> {
    Flux<ScheduleExceptionEntity> findAllBySchoolId(String schoolId);

    Flux<ScheduleExceptionEntity> findAllByClassScheduleId(String classScheduleId);

    Flux<ScheduleExceptionEntity> findAllBySchoolIdAndExceptionDate(
            String schoolId, java.time.LocalDate exceptionDate);
}
