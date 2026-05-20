package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@EqualsAndHashCode(callSuper = true)
@Data
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
