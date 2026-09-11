package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleException extends BaseDomainEntity {
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
}
