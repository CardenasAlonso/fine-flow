package pe.edu.fineflow.identity.domain.model;

import java.time.Instant;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
public class RefreshToken extends BaseDomainEntity {
    public RefreshToken(String id, String schoolId, Instant createdAt, String userId, String tokenHash, String jti, String deviceInfo, String ipAddress, Instant revokedAt, String revokeReason, Instant expiresAt) {
        this.id = id;
        this.schoolId = schoolId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.jti = jti;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.revokedAt = revokedAt;
        this.revokeReason = revokeReason;
        this.expiresAt = expiresAt;
    }
    private String userId;
    private String tokenHash;
    private String jti;
    private String deviceInfo;
    private String ipAddress;
    private Instant revokedAt;
    private String revokeReason;
    private Instant expiresAt;
}
