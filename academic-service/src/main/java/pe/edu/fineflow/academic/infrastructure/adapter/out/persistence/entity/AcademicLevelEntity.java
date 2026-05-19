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
@Table("ACADEMIC_LEVELS")
public class AcademicLevelEntity extends BaseTenantEntity {
    @Column("NAME")
    private String name;

    @Column("ORDER_NUM")
    private Integer orderNum;

    @Column("IS_ACTIVE")
    private Integer isActive;
}
