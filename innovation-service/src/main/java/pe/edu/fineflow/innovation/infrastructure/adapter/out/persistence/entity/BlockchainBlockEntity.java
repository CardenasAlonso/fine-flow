package pe.edu.fineflow.innovation.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("BLOCKCHAIN_BLOCKS")
@Data
@NoArgsConstructor
public class BlockchainBlockEntity extends BaseTenantEntity {
    @Column("EVENT_TYPE")
    private String eventType;

    @Column("ENTITY_ID")
    private String entityId;

    @Column("ENTITY_TYPE")
    private String entityType;

    @Column("PAYLOAD")
    private String payload;

    @Column("PREVIOUS_HASH")
    private String previousHash;

    @Column("HASH")
    private String hash;

    @Column("CREATED_BY")
    private String createdBy;

    @Column("BLOCK_INDEX")
    private int blockIndex;
}
