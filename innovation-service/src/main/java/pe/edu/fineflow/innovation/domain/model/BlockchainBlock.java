package pe.edu.fineflow.innovation.domain.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
public class BlockchainBlock extends BaseDomainEntity {
    public BlockchainBlock(String id, String schoolId, Instant createdAt, String eventType, String entityId, String entityType, String payload, String previousHash, String hash, String createdBy, int blockIndex) {
        this.id = id;
        this.schoolId = schoolId;
        this.createdAt = createdAt;
        this.eventType = eventType;
        this.entityId = entityId;
        this.entityType = entityType;
        this.payload = payload;
        this.previousHash = previousHash;
        this.hash = hash;
        this.createdBy = createdBy;
        this.blockIndex = blockIndex;
    }
    private String eventType;
    private String entityId;
    private String entityType;
    private String payload;
    private String previousHash;
    private String hash;
    private String createdBy;
    private int blockIndex;
}
