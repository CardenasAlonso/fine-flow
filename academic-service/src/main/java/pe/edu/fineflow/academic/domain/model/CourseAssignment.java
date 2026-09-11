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
public class CourseAssignment extends BaseDomainEntity {
    private String courseId;
    private String sectionId;
    private String teacherId;
    private String academicPeriodId;
    private Integer hoursPerWeek;
    private Integer isActive;
}
