package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("STUDENT_SECTION_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSectionHistoryEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

    @Column("STUDENT_ID")
    private String studentId;

    @Column("FROM_SECTION_ID")
    private String fromSectionId;

    @Column("TO_SECTION_ID")
    private String toSectionId;

    @Column("CHANGE_REASON")
    private String changeReason;

    @Column("NOTES")
    private String notes;

    @Column("CHANGED_BY")
    private String changedBy;

    @Column("CHANGED_AT")
    private Instant changedAt;

    @Column("EFFECTIVE_DATE")
    private LocalDate effectiveDate;
}