package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("REFRESH_TOKENS")
public class RefreshTokenEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

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

    @Column("IS_REVOKED")
    private Integer isRevoked;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

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

    public Integer getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Integer isRevoked) {
        this.isRevoked = isRevoked;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
