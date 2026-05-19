package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.math.BigDecimal;
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
@Table("CLASS_TASKS")
public class ClassTaskEntity extends BaseTenantEntity {
    @Column("COURSE_ASSIGNMENT_ID")
    private String courseAssignmentId;

    @Column("COMPETENCY_ID")
    private String competencyId;

    @Column("ACADEMIC_PERIOD_ID")
    private String academicPeriodId;

    @Column("TITLE")
    private String title;

    @Column("DESCRIPTION")
    private String description;

    @Column("TASK_TYPE")
    private String taskType;

    @Column("MAX_SCORE")
    private BigDecimal maxScore;

    @Column("DUE_DATE")
    private LocalDate dueDate;

    @Column("IS_ACTIVE")
    private Integer isActive;

    @Version
    @Column("VERSION")
    private Long version;
}
