package pe.edu.fineflow.identity.domain.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
public class User extends BaseDomainEntity {
    private String email;
    private String passwordHash;
    private String role;
    private String firstName;
    private String lastName;
    private String status;
    private Instant lastLoginAt;
    private Instant updatedAt;
}
