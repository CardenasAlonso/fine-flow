package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Data
@NoArgsConstructor
@Table("USERS")
public class UserEntity extends BaseTenantEntity {
    @Column("EMAIL")
    private String email;

    @Column("PASSWORD_HASH")
    private String passwordHash;

    @Column("ROLE")
    private String role;

    @Column("FIRST_NAME")
    private String firstName;

    @Column("LAST_NAME")
    private String lastName;

    @Column("STATUS")
    private String status;

    @Column("LAST_LOGIN_AT")
    private Instant lastLoginAt;

    @LastModifiedDate
    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Version
    @Column("VERSION")
    private Long version;
}
