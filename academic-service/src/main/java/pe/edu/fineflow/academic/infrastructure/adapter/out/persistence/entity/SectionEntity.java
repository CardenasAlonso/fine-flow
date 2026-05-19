package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("SECTIONS")
public class SectionEntity extends BaseTenantEntity {
    @Column("SCHOOL_YEAR_ID")
    private String schoolYearId;

    @Column("NAME")
    private String name;

    @Column("MAX_CAPACITY")
    private Integer maxCapacity;

    @Column("TUTOR_ID")
    private String tutorId;

    @Column("IS_ACTIVE")
    private Integer isActive;

    @Version
    @Column("VERSION")
    private Long version;
}
