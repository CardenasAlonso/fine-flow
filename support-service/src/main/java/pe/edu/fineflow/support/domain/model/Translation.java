package pe.edu.fineflow.support.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Translation {
    private String id;
    private String transKey;
    private String langCode;
    private String value;
    private String context;
    private Instant updatedAt;
}