package pe.edu.fineflow.profile.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private String id;
    private String schoolId;
    private String userId;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String specialty;
    private String phone;
    private String status;
    private Instant createdAt;
}
