package pe.edu.fineflow.academic.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    private String id;
    private String schoolId;
    private Integer slotNumber;
    private String slotName;
    private String startTime;
    private String endTime;
    private Integer durationMin;
    private String slotType;
    private Integer isActive;
}
