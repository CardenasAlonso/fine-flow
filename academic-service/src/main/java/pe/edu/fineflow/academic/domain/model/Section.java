package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Section extends BaseDomainEntity {
    private String schoolYearId;
    private String name;
    private Integer maxCapacity;
    private String tutorId;
    private Integer isActive;
}
