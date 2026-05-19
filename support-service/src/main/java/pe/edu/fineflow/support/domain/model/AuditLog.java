package pe.edu.fineflow.support.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseDomainEntity {
    private String userId;
    private String action;
    private String entityType;
    private String entityId;
    private String oldValueJson;
    private String newValueJson;
    private String ipAddress;
    private String userAgent;
    private String result;
    private String errorDetail;
    private Long durationMs;
}
