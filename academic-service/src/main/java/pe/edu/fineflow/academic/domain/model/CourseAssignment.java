package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
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
