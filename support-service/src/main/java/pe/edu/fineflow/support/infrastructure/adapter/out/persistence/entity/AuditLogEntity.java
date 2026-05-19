package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("AUDIT_LOGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity extends BaseTenantEntity {
    @Column("USER_ID")
    private String userId;

    @Column("ACTION")
    private String action;

    @Column("ENTITY_TYPE")
    private String entityType;

    @Column("ENTITY_ID")
    private String entityId;

    @Column("OLD_VALUE_JSON")
    private String oldValueJson;

    @Column("NEW_VALUE_JSON")
    private String newValueJson;

    @Column("IP_ADDRESS")
    private String ipAddress;

    @Column("USER_AGENT")
    private String userAgent;

    @Column("RESULT")
    private String result;

    @Column("ERROR_DETAIL")
    private String errorDetail;

    @Column("DURATION_MS")
    private Long durationMs;

}
