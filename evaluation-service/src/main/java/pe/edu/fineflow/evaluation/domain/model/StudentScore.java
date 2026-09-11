package pe.edu.fineflow.evaluation.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentScore {
    private String id;
    private String schoolId;
    private String studentId;
    private String classTaskId;
    private String registeredBy;
    private String comments;
    private BigDecimal score;
    private Instant registeredAt;
    private Instant updatedAt;

    public boolean isWithinRange() {
        return score != null && score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(BigDecimal.valueOf(20)) <= 0;
    }
}