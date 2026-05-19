package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("COURSE_COMPETENCIES")
public class CourseCompetencyEntity extends BaseTenantEntity {
    @Column("COURSE_ID")
    private String courseId;

    @Column("NAME")
    private String name;

    @Column("DESCRIPTION")
    private String description;

    @Column("WEIGHT")
    private BigDecimal weight;

    @Column("IS_ACTIVE")
    private Integer isActive;
}
