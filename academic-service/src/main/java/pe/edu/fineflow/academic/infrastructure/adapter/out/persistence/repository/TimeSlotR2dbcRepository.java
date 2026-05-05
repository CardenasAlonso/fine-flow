package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.TimeSlotEntity;
import reactor.core.publisher.Flux;

@Repository
public interface TimeSlotR2dbcRepository extends R2dbcRepository<TimeSlotEntity, String> {
    Flux<TimeSlotEntity> findAllBySchoolId(String schoolId);

    Flux<TimeSlotEntity> findAllBySchoolIdAndSlotType(String schoolId, String slotType);

    Flux<TimeSlotEntity> findAllBySchoolIdAndIsActiveOrderBySlotNumber(
            String schoolId, Integer isActive);

    @Query(
            "SELECT * FROM TIME_SLOTS WHERE school_id = :schoolId "
                    + "OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<TimeSlotEntity> findAllBySchoolId(
            String schoolId, int offset, int limit);

    @Query(
            "SELECT * FROM TIME_SLOTS WHERE school_id = :schoolId AND is_active = :isActive "
                    + "OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<TimeSlotEntity> findAllBySchoolIdAndIsActive(
            String schoolId, Integer isActive, int offset, int limit);
}
