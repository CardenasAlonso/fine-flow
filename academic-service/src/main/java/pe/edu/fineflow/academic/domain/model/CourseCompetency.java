package pe.edu.fineflow.academic.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCompetency extends BaseDomainEntity {
    private String courseId;
    private String name;
    private String description;
    private BigDecimal weight;
    private Integer isActive;
}
