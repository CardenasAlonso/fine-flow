package pe.edu.fineflow.academic.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class ClassTask extends BaseDomainEntity {

    private String courseAssignmentId;
    private String competencyId;
    private String academicPeriodId;
    private String title;
    private String description;
    private String taskType;
    private BigDecimal maxScore;
    private LocalDate dueDate;
    private Integer isActive;

    public void activate() {
        this.isActive = 1;
    }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now());
    }
}
