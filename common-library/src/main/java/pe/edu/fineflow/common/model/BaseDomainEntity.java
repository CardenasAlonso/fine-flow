package pe.edu.fineflow.common.model;

import java.time.Instant;
import lombok.Data;

@Data
public abstract class BaseDomainEntity {
    protected String id;
    protected String schoolId;
    protected Instant createdAt;
}
