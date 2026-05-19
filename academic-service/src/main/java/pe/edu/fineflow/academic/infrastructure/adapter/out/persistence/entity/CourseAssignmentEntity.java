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
@Table("COURSE_ASSIGNMENTS")
public class CourseAssignmentEntity extends BaseTenantEntity {
    @Column("COURSE_ID")
    private String courseId;

    @Column("SECTION_ID")
    private String sectionId;

    @Column("TEACHER_ID")
    private String teacherId;

    @Column("ACADEMIC_PERIOD_ID")
    private String academicPeriodId;

    @Column("HOURS_PER_WEEK")
    private Integer hoursPerWeek;

    @Column("IS_ACTIVE")
    private Integer isActive;

    @Version
    @Column("VERSION")
    private Long version;
}
