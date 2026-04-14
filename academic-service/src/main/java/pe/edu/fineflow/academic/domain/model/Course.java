package pe.edu.fineflow.academic.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String id;
    private String schoolId;
    private String name;
    private String code;
    private String description;
    private String colorHex;
    private Integer isActive;
    private Instant createdAt;
}
