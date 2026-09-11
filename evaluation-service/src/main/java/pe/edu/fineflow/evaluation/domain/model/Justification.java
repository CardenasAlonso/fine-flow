package pe.edu.fineflow.evaluation.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}