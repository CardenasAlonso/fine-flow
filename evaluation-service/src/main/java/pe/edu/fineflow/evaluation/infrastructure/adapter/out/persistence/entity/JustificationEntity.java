package pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("JUSTIFICATIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificationEntity extends BaseTenantEntity {
    @Column("STUDENT_ID")
    private String studentId;

    @Column("ATTENDANCE_ID")
    private String attendanceId;

    @Column("REQUESTED_BY")
    private String requestedBy;

    @Column("REASON")
    private String reason;

    @Column("DOCUMENT_URL")
    private String documentUrl;

    @Column("STATUS")
    private String status;

    @Column("REVIEWED_BY")
    private String reviewedBy;

    @Column("REVIEW_NOTE")
    private String reviewNote;

    @Column("AUTO_APPROVED")
    private Integer autoApproved;

    @Column("REQUESTED_AT")
    private Instant requestedAt;

    @Column("REVIEWED_AT")
    private Instant reviewedAt;

    @Column("EXPIRES_AT")
    private Instant expiresAt;
}