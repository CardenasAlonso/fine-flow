package pe.edu.fineflow.evaluation.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class Justification extends BaseDomainEntity {
    private String studentId;
    private String attendanceId;
    private String requestedBy;
    private String reason;
    private String documentUrl;
    private String status;
    private String reviewedBy;
    private String reviewNote;
    private Integer autoApproved;
    private Instant requestedAt;
    private Instant reviewedAt;
    private Instant expiresAt;

    public void approve(String reviewerId) {
        this.status = "APPROVED";
        this.reviewedBy = reviewerId;
        this.reviewedAt = Instant.now();
    }

    public void reject(String reviewerId, String note) {
        this.status = "REJECTED";
        this.reviewedBy = reviewerId;
        this.reviewNote = note;
        this.reviewedAt = Instant.now();
    }

    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
}