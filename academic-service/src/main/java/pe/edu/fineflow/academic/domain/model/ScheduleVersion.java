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
public class ScheduleVersion extends BaseDomainEntity {
    private String schoolYearId;
    private String academicPeriodId;
    private String versionName;
    private String status;
    private String notes;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private Instant publishedAt;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Instant updatedAt;
}
