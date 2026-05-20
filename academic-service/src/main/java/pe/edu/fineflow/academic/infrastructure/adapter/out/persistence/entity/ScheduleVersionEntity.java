package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("SCHEDULE_VERSIONS")
public class ScheduleVersionEntity extends BaseTenantEntity {
    @Column("SCHOOL_YEAR_ID")
    private String schoolYearId;

    @Column("ACADEMIC_PERIOD_ID")
    private String academicPeriodId;

    @Column("VERSION_NAME")
    private String versionName;

    @Column("STATUS")
    private String status;

    @Column("NOTES")
    private String notes;

    @Column("CREATED_BY")
    private String createdBy;

    @Column("APPROVED_BY")
    private String approvedBy;

    @Column("APPROVED_AT")
    private Instant approvedAt;

    @Column("PUBLISHED_AT")
    private Instant publishedAt;

    @Column("VALID_FROM")
    private LocalDate validFrom;

    @Column("VALID_UNTIL")
    private LocalDate validUntil;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
