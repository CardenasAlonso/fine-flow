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
public class Course extends BaseDomainEntity {
    private String name;
    private String code;
    private String description;
    private String colorHex;
    private Integer isActive;
}
