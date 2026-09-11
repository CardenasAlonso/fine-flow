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
public class AcademicLevel extends BaseDomainEntity {
    private String name;
    private Integer orderNum;
    private Integer isActive;
}
