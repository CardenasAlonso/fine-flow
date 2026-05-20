package pe.edu.fineflow.common.model;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseVersionedDomainEntity extends BaseDomainEntity {
    private Long version;
    private Instant updatedAt;
}
