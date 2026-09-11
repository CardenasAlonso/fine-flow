package pe.edu.fineflow.academic.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class Section extends BaseDomainEntity {

    private String schoolYearId;
    private String name;
    private Integer maxCapacity;
    private String tutorId;
    private Integer isActive;

    public void activate() {
        this.isActive = 1;
    }

    public boolean isFull(int currentEnrollment) {
        return maxCapacity != null && currentEnrollment >= maxCapacity;
    }
}
