package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolYear extends BaseDomainEntity {
    private String academicLevelId;
    private String name;
    private Integer gradeNumber;
    private Integer calendarYear;
    private Integer isActive;
}
