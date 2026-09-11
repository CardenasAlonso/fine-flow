package pe.edu.fineflow.academic.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class TimeSlot extends BaseDomainEntity {

    private Integer slotNumber;
    private String slotName;
    private String startTime;
    private String endTime;
    private Integer durationMin;
    private String slotType;
    private Integer isActive;

    public boolean isBreak() {
        return "BREAK".equalsIgnoreCase(this.slotType);
    }

    public void activate() {
        this.isActive = 1;
    }
}
