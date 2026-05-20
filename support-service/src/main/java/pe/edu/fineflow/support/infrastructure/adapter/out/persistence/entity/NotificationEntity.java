package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("NOTIFICATIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends BaseTenantEntity {
    @Column("USER_ID")
    private String userId;

    @Column("TARGET_ROLE")
    private String targetRole;

    @Column("NOTIFICATION_TYPE")
    private String notificationType;

    @Column("TITLE")
    private String title;

    @Column("BODY")
    private String body;

    @Column("ACTION_URL")
    private String actionUrl;

    @Column("METADATA_JSON")
    private String metadataJson;

    @Column("IS_READ")
    private Integer isRead;

    @Column("READ_AT")
    private Instant readAt;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

}
