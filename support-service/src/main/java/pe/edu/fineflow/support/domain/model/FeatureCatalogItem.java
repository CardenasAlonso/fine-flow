package pe.edu.fineflow.support.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureCatalogItem {
    private String featureKey;
    private String name;
    private String description;
    private String category;
    private Integer enabledByDefault;
    private String minPlan;
    private Integer isCore;
    private Integer sortOrder;
}