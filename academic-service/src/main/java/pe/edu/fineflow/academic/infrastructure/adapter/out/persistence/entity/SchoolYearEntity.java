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
@Table("SCHOOL_YEARS")
public class SchoolYearEntity extends BaseTenantEntity {
    @Column("ACADEMIC_LEVEL_ID")
    private String academicLevelId;

    @Column("NAME")
    private String name;

    @Column("GRADE_NUMBER")
    private Integer gradeNumber;

    @Column("CALENDAR_YEAR")
    private Integer calendarYear;

    @Column("IS_ACTIVE")
    private Integer isActive;
}
