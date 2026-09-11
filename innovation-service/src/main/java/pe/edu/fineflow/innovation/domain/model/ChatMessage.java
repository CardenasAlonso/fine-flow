package pe.edu.fineflow.innovation.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private String id;
    private String schoolId;
    private String sessionId;
    private String role;
    private String content;
    private String sourcesJson;
    private BigDecimal confidence;
    private Instant createdAt;
}