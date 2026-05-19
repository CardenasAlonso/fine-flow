package pe.edu.fineflow.common.model;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class VersionedTenantEntity extends BaseTenantEntity {
    @Version
    @Column("VERSION")
    private Long version;

    @LastModifiedDate
    @Column("UPDATED_AT")
    private Instant updatedAt;
}
