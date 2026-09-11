package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStoreEntry extends BaseDomainEntity {
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private Integer eventVersion;
    private String payloadJson;
    private String metadataJson;
    private String causationId;
    private String correlationId;
    private Instant occurredAt;
}