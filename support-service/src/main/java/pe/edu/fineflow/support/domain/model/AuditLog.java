package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private String id;
    private String schoolId;
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
    private Instant createdAt;
}
