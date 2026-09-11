package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

    public static StudentSectionHistory record(String studentId, String fromSectionId,
                                                String toSectionId, String changeReason,
                                                String changedBy) {
        StudentSectionHistory history = new StudentSectionHistory();
        history.setStudentId(studentId);
        history.setFromSectionId(fromSectionId);
        history.setToSectionId(toSectionId);
        history.setChangeReason(changeReason);
        history.setChangedBy(changedBy);
        history.setChangedAt(Instant.now());
        return history;
    }

    public boolean hasSameSection() {
        return fromSectionId != null && fromSectionId.equals(toSectionId);
    }
}
