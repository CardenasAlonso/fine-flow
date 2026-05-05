package pe.edu.fineflow.identity.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String id;
    private String schoolId;
    private String userId;
    private String tokenHash;
    private String jti;
    private String deviceInfo;
    private String ipAddress;
    private Integer isRevoked;
    private Instant expiresAt;
    private Instant createdAt;
}
