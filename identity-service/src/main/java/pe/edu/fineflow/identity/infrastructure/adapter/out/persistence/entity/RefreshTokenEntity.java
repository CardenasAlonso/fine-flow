package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("REFRESH_TOKENS")
public class RefreshTokenEntity extends BaseTenantEntity {
    @Column("USER_ID")
    private String userId;

    @Column("TOKEN_HASH")
    private String tokenHash;

    @Column("JTI")
    private String jti;

    @Column("DEVICE_INFO")
    private String deviceInfo;

    @Column("IP_ADDRESS")
    private String ipAddress;

    @Column("REVOKED_AT")
    private Instant revokedAt;

    @Column("REVOKE_REASON")
    private String revokeReason;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
