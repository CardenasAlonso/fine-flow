package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseDomainEntity {
    private String name;
    private String code;
    private String description;
    private String colorHex;
    private Integer isActive;
}
