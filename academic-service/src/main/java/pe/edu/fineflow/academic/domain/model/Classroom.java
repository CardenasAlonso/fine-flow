package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {
    private String id;
    private String schoolId;
    private String name;
    private String roomType;
    private Integer capacity;
    private Integer floorNumber;
    private String building;
    private Integer hasProjector;
    private Integer hasComputers;
    private Integer isActive;
    private String notes;
    private Instant createdAt;
}
