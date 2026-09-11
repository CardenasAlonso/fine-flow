package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
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

    public void markAsRead() {
        this.isRead = 1;
        this.readAt = Instant.now();
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}