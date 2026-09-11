package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("TEACHER_SPECIALTIES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSpecialtyEntity extends BaseTenantEntity {
    @Column("TEACHER_ID")
    private String teacherId;

    @Column("SUBJECT")
    private String subject;

    @Column("SPECIALTY_LEVEL")
    private String specialtyLevel;

    @Column("IS_PRIMARY")
    private Integer isPrimary;

    @Column("CERTIFIED")
    private Integer certified;
}