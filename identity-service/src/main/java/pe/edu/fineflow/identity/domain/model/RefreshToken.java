package pe.edu.fineflow.identity.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseDomainEntity {

    private String userId;
    private String tokenHash;
    private String jti;
    private String deviceInfo;
    private String ipAddress;
    private Instant revokedAt;
    private String revokeReason;
    private Instant expiresAt;

    public static RefreshToken create(String userId, String schoolId, String tokenHash,
                                       String jti, Instant expiresAt) {
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setSchoolId(schoolId);
        token.setTokenHash(tokenHash);
        token.setJti(jti);
        token.setExpiresAt(expiresAt);
        return token;
    }

    public RefreshToken renew(String newTokenHash, String newJti, Instant newExpiresAt) {
        RefreshToken renewed = new RefreshToken();
        renewed.setSchoolId(this.schoolId);
        renewed.setUserId(this.userId);
        renewed.setTokenHash(newTokenHash);
        renewed.setJti(newJti);
        renewed.setDeviceInfo(this.deviceInfo);
        renewed.setIpAddress(this.ipAddress);
        renewed.setExpiresAt(newExpiresAt);
        return renewed;
    }

    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(Instant.now());
    }

    public boolean isRevoked() {
        return this.revokedAt != null;
    }

    public boolean isValid() {
        return !isRevoked() && !isExpired();
    }

    public void revoke() {
        this.revokedAt = Instant.now();
    }
}