package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig extends BaseDomainEntity {
    private String configKey;
    private String configValue;
    private String valueType;
    private String description;
    private Integer isSensitive;
    private String updatedBy;
    private Instant updatedAt;
}