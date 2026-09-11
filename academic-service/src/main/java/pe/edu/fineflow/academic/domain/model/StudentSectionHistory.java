package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSectionHistory {
    private String id;
    private String schoolId;
    private String studentId;
    private String fromSectionId;
    private String toSectionId;
    private String changeReason;
    private String notes;
    private String changedBy;
    private Instant changedAt;
    private LocalDate effectiveDate;
}