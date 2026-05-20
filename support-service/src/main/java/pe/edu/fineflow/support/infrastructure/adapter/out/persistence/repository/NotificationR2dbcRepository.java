package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationR2dbcRepository extends R2dbcRepository<NotificationEntity, String> {
    Flux<NotificationEntity> findByUserIdAndSchoolIdAndIsRead(
            String userId, String schoolId, Integer isRead);

    Flux<NotificationEntity> findByTargetRoleAndSchoolId(String role, String schoolId);

    Flux<NotificationEntity> findByUserIdAndSchoolId(String userId, String schoolId);

    @Query("SELECT * FROM NOTIFICATIONS WHERE school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<NotificationEntity> findAllBySchoolId(String schoolId, int offset, int limit);
}
