package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseDomainEntity {
    private String userId;
    private String targetRole;
    private String notificationType;
    private String title;
    private String body;
    private String actionUrl;
    private String metadataJson;
    private Integer isRead;
    private Instant readAt;
    private Instant expiresAt;
}
