package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class FeatureFlag extends BaseDomainEntity {
    private String featureName;
    private Integer enabled;
    private String description;
    private String planRequired;
    private Integer rolloutPct;
    private Instant expiresAt;

    public boolean isEnabled() {
        return enabled != null && enabled == 1;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}