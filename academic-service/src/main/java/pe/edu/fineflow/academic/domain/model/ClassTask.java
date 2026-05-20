package pe.edu.fineflow.academic.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
