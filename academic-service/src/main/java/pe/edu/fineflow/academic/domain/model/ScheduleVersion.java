package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleVersion extends BaseDomainEntity {

    public enum Status { DRAFT, REVIEW, ACTIVE, ARCHIVED }

    private String schoolYearId;
    private String academicPeriodId;
    private String versionName;
    private Status status;
    private String notes;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private Instant publishedAt;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Instant updatedAt;

    public void markAsReview() {
        if (this.status != Status.DRAFT) {
            throw new IllegalStateException("Solo los horarios en DRAFT pueden enviarse a revisión");
        }
        this.status = Status.REVIEW;
        this.updatedAt = Instant.now();
    }

    public void publish() {
        if (this.status != Status.DRAFT && this.status != Status.REVIEW) {
            throw new IllegalStateException("Solo se pueden publicar horarios en estado DRAFT o REVIEW");
        }
        this.status = Status.ACTIVE;
        this.publishedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.status = Status.ARCHIVED;
        this.updatedAt = Instant.now();
    }

    public boolean isModifiable() {
        return this.status != Status.ACTIVE;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }
}
