package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot extends BaseDomainEntity {
    private Integer slotNumber;
    private String slotName;
    private String startTime;
    private String endTime;
    private Integer durationMin;
    private String slotType;
    private Integer isActive;
}
