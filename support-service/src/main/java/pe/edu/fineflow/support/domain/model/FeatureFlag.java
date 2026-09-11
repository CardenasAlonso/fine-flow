package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlag extends BaseDomainEntity {
    private String featureName;
    private Integer enabled;
    private String description;
    private String planRequired;
    private Integer rolloutPct;
    private Instant expiresAt;
}