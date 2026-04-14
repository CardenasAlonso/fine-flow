package pe.edu.fineflow.academic.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCompetency {
    private String id;
    private String schoolId;
    private String courseId;
    private String name;
    private String description;
    private BigDecimal weight;
    private Integer isActive;
    private Instant createdAt;
}
