package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleException {
    private String id;
    private String schoolId;
    private String classScheduleId;
    private LocalDate exceptionDate;
    private String exceptionType;
    private String substituteTeacherId;
    private String substituteClassroomId;
    private String substituteSlotId;
    private String reason;
    private String approvedBy;
    private Instant approvedAt;
    private String createdBy;
    private Instant createdAt;
}
