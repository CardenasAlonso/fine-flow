package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("TEACHERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherEntity extends BaseTenantEntity {
    @Column("USER_ID")
    private String userId;

    @Column("FIRST_NAME")
    private String firstName;

    @Column("LAST_NAME")
    private String lastName;

    @Column("DOCUMENT_NUMBER")
    private String documentNumber;

    @Column("SPECIALTY")
    private String specialty;

    @Column("PHONE")
    private String phone;

    @Column("STATUS")
    private String status;

    @LastModifiedDate
    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Version
    @Column("VERSION")
    private Long version;
}
