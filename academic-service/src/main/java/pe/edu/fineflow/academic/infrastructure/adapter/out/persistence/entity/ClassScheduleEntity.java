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
@Table("CLASS_SCHEDULES")
public class ClassScheduleEntity extends BaseTenantEntity {
    @Column("SCHEDULE_VERSION_ID")
    private String scheduleVersionId;

    @Column("COURSE_ASSIGNMENT_ID")
    private String courseAssignmentId;

    @Column("SECTION_ID")
    private String sectionId;

    @Column("TEACHER_ID")
    private String teacherId;

    @Column("CLASSROOM_ID")
    private String classroomId;

    @Column("TIME_SLOT_ID")
    private String timeSlotId;

    @Column("DAY_OF_WEEK")
    private Integer dayOfWeek;

    @Column("WEEK_TYPE")
    private String weekType;

    @Column("COLOR_HEX")
    private String colorHex;

    @Column("NOTES")
    private String notes;

    @Column("IS_ACTIVE")
    private Integer isActive;

    @Column("CREATED_BY")
    private String createdBy;
}
