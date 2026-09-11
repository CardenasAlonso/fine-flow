package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("EVENT_STORE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStoreEntity extends BaseTenantEntity {
    @Column("AGGREGATE_TYPE")
    private String aggregateType;

    @Column("AGGREGATE_ID")
    private String aggregateId;

    @Column("EVENT_TYPE")
    private String eventType;

    @Column("EVENT_VERSION")
    private Integer eventVersion;

    @Column("PAYLOAD_JSON")
    private String payloadJson;

    @Column("METADATA_JSON")
    private String metadataJson;

    @Column("CAUSATION_ID")
    private String causationId;

    @Column("CORRELATION_ID")
    private String correlationId;

    @Column("OCCURRED_AT")
    private Instant occurredAt;
}