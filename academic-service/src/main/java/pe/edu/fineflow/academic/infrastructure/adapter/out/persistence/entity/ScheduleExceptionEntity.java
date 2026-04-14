package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("SCHEDULE_EXCEPTIONS")
public class ScheduleExceptionEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

    @Column("CLASS_SCHEDULE_ID")
    private String classScheduleId;

    @Column("EXCEPTION_DATE")
    private LocalDate exceptionDate;

    @Column("EXCEPTION_TYPE")
    private String exceptionType;

    @Column("SUBSTITUTE_TEACHER_ID")
    private String substituteTeacherId;

    @Column("SUBSTITUTE_CLASSROOM_ID")
    private String substituteClassroomId;

    @Column("SUBSTITUTE_SLOT_ID")
    private String substituteSlotId;

    @Column("REASON")
    private String reason;

    @Column("APPROVED_BY")
    private String approvedBy;

    @Column("APPROVED_AT")
    private Instant approvedAt;

    @Column("CREATED_BY")
    private String createdBy;

    @Column("CREATED_AT")
    private Instant createdAt;
}
