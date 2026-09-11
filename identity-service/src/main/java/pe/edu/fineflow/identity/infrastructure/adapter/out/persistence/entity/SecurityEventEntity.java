package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("SECURITY_EVENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEventEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

    @Column("USER_ID")
    private String userId;

    @Column("EVENT_TYPE")
    private String eventType;

    @Column("SEVERITY")
    private String severity;

    @Column("DESCRIPTION")
    private String description;

    @Column("IP_ADDRESS")
    private String ipAddress;

    @Column("USER_AGENT")
    private String userAgent;

    @Column("SESSION_TOKEN")
    private String sessionToken;

    @Column("ENTITY_TYPE")
    private String entityType;

    @Column("ENTITY_ID")
    private String entityId;

    @Column("METADATA_JSON")
    private String metadataJson;

    @Column("RESOLVED")
    private Integer resolved;

    @Column("RESOLVED_BY")
    private String resolvedBy;

    @Column("RESOLVED_AT")
    private Instant resolvedAt;

    @Column("CREATED_AT")
    private Instant createdAt;
}