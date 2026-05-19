package pe.edu.fineflow.evaluation.domain.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseDomainEntity {
    private String studentId;
    private String courseAssignmentId;
    private LocalDate attendanceDate;
    private String status;
    private String checkInTime;
    private String recordMethod;
    private String justificationReason;
    private String registeredBy;
}
