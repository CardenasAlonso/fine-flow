package pe.edu.fineflow.evaluation.domain.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
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