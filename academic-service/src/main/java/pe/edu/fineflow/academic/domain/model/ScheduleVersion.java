package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVersion {
    private String id;
    private String schoolId;
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
    private Instant createdAt;
    private Instant updatedAt;
}
