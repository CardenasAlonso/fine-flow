package pe.edu.fineflow.profile.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
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
}
