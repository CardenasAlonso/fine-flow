package pe.edu.fineflow.profile.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends BaseDomainEntity {
    private String userId;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String specialty;
    private String phone;
    private String status;

    public void activate() {
        this.status = "ACTIVE";
    }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}