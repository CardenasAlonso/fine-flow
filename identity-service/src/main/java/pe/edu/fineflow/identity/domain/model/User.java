package pe.edu.fineflow.identity.domain.model;

import java.time.Instant;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseDomainEntity {

    private static final Set<String> SELF_REGISTER_ROLES = Set.of("STUDENT", "GUARDIAN");

    private String email;
    private String passwordHash;
    private String role;
    private String firstName;
    private String lastName;
    private String status;
    private Instant lastLoginAt;
    private Instant updatedAt;

    public static User register(String email, String encodedPassword, String role,
                                 String firstName, String lastName) {
        if (!SELF_REGISTER_ROLES.contains(role)) {
            throw new IllegalArgumentException(
                "Registro solo permitido para estudiantes y tutores");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encodedPassword);
        user.setRole(role);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setStatus("ACTIVE");
        return user;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}