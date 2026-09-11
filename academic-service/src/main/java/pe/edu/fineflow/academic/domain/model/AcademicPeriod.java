package pe.edu.fineflow.academic.domain.model;

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
public class AcademicPeriod extends BaseDomainEntity {
    private String schoolYearId;
    private String name;
    private String periodType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer isActive;
}
