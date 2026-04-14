package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleVersionEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ScheduleVersionR2dbcRepository
        extends ReactiveCrudRepository<ScheduleVersionEntity, String> {
    Flux<ScheduleVersionEntity> findAllBySchoolId(String schoolId);

    Flux<ScheduleVersionEntity> findAllBySchoolIdAndStatus(String schoolId, String status);

    Flux<ScheduleVersionEntity> findAllBySchoolIdAndSchoolYearId(
            String schoolId, String schoolYearId);
}
