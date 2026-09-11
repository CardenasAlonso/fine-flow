package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("SCHOOLS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("NAME")
    private String name;

    @Column("STATUS")
    private String status;
}