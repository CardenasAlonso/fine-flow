package pe.edu.fineflow.profile.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseDomainEntity {
    private String sectionId;
    private String userId;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String bloodType;
    private String photoUrl;
    private String qrSecret;
    private String status;
    private LocalDate birthDate;
    private Instant updatedAt;

    public void activate() {
        this.status = "ACTIVE";
    }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}