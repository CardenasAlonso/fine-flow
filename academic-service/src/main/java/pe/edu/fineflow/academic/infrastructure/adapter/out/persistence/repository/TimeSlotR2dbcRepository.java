package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.TimeSlotEntity;
import reactor.core.publisher.Flux;

@Repository
public interface TimeSlotR2dbcRepository extends ReactiveCrudRepository<TimeSlotEntity, String> {
    Flux<TimeSlotEntity> findAllBySchoolId(String schoolId);

    Flux<TimeSlotEntity> findAllBySchoolIdAndSlotType(String schoolId, String slotType);

    Flux<TimeSlotEntity> findAllBySchoolIdAndIsActiveOrderBySlotNumber(
            String schoolId, Integer isActive);
}
