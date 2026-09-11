package pe.edu.fineflow.academic.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
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

    public void activate() {
        this.isActive = 1;
    }

    public boolean isAvailable(int currentUsage) {
        return capacity != null && currentUsage < capacity;
    }
}
