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
public class Classroom extends BaseDomainEntity {
    private String name;
    private String roomType;
    private Integer capacity;
    private Integer floorNumber;
    private String building;
    private Integer hasProjector;
    private Integer hasComputers;
    private Integer isActive;
    private String notes;
}
