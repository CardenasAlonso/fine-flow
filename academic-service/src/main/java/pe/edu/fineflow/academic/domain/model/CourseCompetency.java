package pe.edu.fineflow.academic.domain.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class CourseCompetency extends BaseDomainEntity {

    private String courseId;
    private String name;
    private String description;
    private BigDecimal weight;
    private Integer isActive;

    public void activate() {
        this.isActive = 1;
    }

    public boolean isValidWeight() {
        return weight != null && weight.compareTo(BigDecimal.ZERO) > 0 && weight.compareTo(BigDecimal.valueOf(100)) <= 0;
    }
}
