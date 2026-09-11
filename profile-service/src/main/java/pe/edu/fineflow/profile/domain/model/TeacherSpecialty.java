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
public class TeacherSpecialty extends BaseDomainEntity {
    private String teacherId;
    private String subject;
    private String specialtyLevel;
    private Integer isPrimary;
    private Integer certified;
}