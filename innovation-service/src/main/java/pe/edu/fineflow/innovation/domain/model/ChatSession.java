package pe.edu.fineflow.innovation.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatSession {
    private String id;
    private String schoolId;
    private String userId;
    private String userRole;
    private String sessionToken;
    private Instant startedAt;
    private Instant lastMessageAt;
    private Instant endedAt;
    private Integer isActive;

    public boolean isActiveSession() {
        return isActive != null && isActive == 1 && endedAt == null;
    }

    public void end() {
        this.isActive = 0;
        this.endedAt = Instant.now();
    }
}