package pe.edu.fineflow.profile.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseDomainEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSpecialty extends BaseDomainEntity {
    private String teacherId;
    private String subject;
    private String specialtyLevel;
    private Integer isPrimary;
    private Integer certified;
}