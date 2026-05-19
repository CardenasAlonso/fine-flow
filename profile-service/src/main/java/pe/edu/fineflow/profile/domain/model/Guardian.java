package pe.edu.fineflow.profile.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guardian extends BaseDomainEntity {
    private String userId;
    private String studentId;
    private String firstName;
    private String lastName;
    private String relationship;
    private String phone;
    private String documentNumber;
    private String email;
    private boolean primaryContact;
}
