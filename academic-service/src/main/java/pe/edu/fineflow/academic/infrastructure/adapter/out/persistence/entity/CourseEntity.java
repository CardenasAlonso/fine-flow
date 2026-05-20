package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

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
@Table("COURSES")
public class CourseEntity extends BaseTenantEntity {
    @Column("NAME")
    private String name;

    @Column("CODE")
    private String code;

    @Column("DESCRIPTION")
    private String description;

    @Column("COLOR_HEX")
    private String colorHex;

    @Column("IS_ACTIVE")
    private Integer isActive;
}
