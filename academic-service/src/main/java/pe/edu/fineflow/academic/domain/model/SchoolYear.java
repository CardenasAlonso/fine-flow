package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolYear extends BaseDomainEntity {
    private String academicLevelId;
    private String name;
    private Integer gradeNumber;
    private Integer calendarYear;
    private Integer isActive;
}
