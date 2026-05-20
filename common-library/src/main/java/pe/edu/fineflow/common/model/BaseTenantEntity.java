package pe.edu.fineflow.common.model;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
public abstract class BaseTenantEntity {
    @Id
    @Column("ID")
    protected String id;

    @Column("SCHOOL_ID")
    protected String schoolId;

    @CreatedDate
    @Column("CREATED_AT")
    protected Instant createdAt;
}
