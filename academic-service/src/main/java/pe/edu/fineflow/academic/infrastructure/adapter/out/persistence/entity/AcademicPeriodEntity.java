package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.time.LocalDate;
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
@Table("ACADEMIC_PERIODS")
public class AcademicPeriodEntity extends BaseTenantEntity {
    @Column("SCHOOL_YEAR_ID")
    private String schoolYearId;

    @Column("NAME")
    private String name;

    @Column("PERIOD_TYPE")
    private String periodType;

    @Column("START_DATE")
    private LocalDate startDate;

    @Column("END_DATE")
    private LocalDate endDate;

    @Column("IS_ACTIVE")
    private Integer isActive;

    @Version
    @Column("VERSION")
    private Long version;
}
