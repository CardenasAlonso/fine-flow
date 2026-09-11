package pe.edu.fineflow.innovation.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class BlockchainBlock extends BaseDomainEntity {

    private String eventType;
    private String entityId;
    private String entityType;
    private String payload;
    private String previousHash;
    private String hash;
    private String createdBy;
    private int blockIndex;

    public static BlockchainBlock genesis(String schoolId, String createdBy) {
        BlockchainBlock block = new BlockchainBlock();
        block.setSchoolId(schoolId);
        block.setCreatedBy(createdBy);
        block.setBlockIndex(0);
        block.setEventType("GENESIS");
        block.setPreviousHash("0");
        block.setHash("0");
        return block;
    }

    public boolean hasValidChainLink(String previousBlockHash) {
        return this.previousHash.equals(previousBlockHash);
    }
}