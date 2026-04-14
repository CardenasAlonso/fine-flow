package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSchedule {
    private String id;
    private String schoolId;
    private String scheduleVersionId;
    private String courseAssignmentId;
    private String sectionId;
    private String teacherId;
    private String classroomId;
    private String timeSlotId;
    private Integer dayOfWeek;
    private String weekType;
    private String colorHex;
    private String notes;
    private Integer isActive;
    private String createdBy;
    private Instant createdAt;
}
