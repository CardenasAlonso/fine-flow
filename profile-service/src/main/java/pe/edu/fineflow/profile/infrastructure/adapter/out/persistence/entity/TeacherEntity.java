package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("TEACHERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherEntity {
    @Id private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

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

    @CreatedDate
    @Column("CREATED_AT")
    private Instant createdAt;
}
