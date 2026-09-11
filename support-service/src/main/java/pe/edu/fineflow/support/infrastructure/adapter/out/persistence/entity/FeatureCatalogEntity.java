package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("FEATURE_CATALOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureCatalogEntity {
    @Id
    @Column("FEATURE_KEY")
    private String featureKey;

    @Column("NAME")
    private String name;

    @Column("DESCRIPTION")
    private String description;

    @Column("CATEGORY")
    private String category;

    @Column("ENABLED_BY_DEFAULT")
    private Integer enabledByDefault;

    @Column("MIN_PLAN")
    private String minPlan;

    @Column("IS_CORE")
    private Integer isCore;

    @Column("SORT_ORDER")
    private Integer sortOrder;
}