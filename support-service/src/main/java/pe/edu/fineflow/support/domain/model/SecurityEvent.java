package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEvent extends BaseDomainEntity {
    private String userId;
    private String eventType;
    private String severity;
    private String description;
    private String ipAddress;
    private String userAgent;
    private String sessionToken;
    private String entityType;
    private String entityId;
    private String metadataJson;
    private Integer resolved;
    private String resolvedBy;
    private Instant resolvedAt;
}