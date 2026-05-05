package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import reactor.core.publisher.Flux;

@Repository
public interface AuditLogR2dbcRepository extends R2dbcRepository<AuditLogEntity, String> {
    Flux<AuditLogEntity> findBySchoolIdAndAction(String schoolId, String action);

    Flux<AuditLogEntity> findBySchoolIdAndUserId(String schoolId, String userId);

    @Query("SELECT * FROM AUDIT_LOGS OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AuditLogEntity> findAll(int offset, int limit);

    @Query("SELECT * FROM AUDIT_LOGS WHERE action = :action OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AuditLogEntity> findByAction(String action, int offset, int limit);

    @Query("SELECT * FROM AUDIT_LOGS WHERE user_id = :userId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AuditLogEntity> findByUserId(String userId, int offset, int limit);
}
