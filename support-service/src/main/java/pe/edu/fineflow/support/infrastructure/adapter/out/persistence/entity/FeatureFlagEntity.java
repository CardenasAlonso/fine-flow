package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("FEATURE_FLAGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagEntity extends BaseTenantEntity {
    @Column("FEATURE_NAME")
    private String featureName;

    @Column("ENABLED")
    private Integer enabled;

    @Column("DESCRIPTION")
    private String description;

    @Column("PLAN_REQUIRED")
    private String planRequired;

    @Column("ROLLOUT_PCT")
    private Integer rolloutPct;

    @Column("EXPIRES_AT")
    private Instant expiresAt;
}