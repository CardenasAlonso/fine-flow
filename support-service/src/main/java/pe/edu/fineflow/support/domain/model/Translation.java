package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Translation {
    private String id;
    private String transKey;
    private String langCode;
    private String value;
    private String context;
    private Instant updatedAt;
}