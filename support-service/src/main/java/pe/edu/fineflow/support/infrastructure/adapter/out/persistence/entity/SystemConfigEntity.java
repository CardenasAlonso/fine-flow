package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.fineflow.common.model.BaseTenantEntity;

@Table("SYSTEM_CONFIG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigEntity extends BaseTenantEntity {
    @Column("CONFIG_KEY")
    private String configKey;

    @Column("CONFIG_VALUE")
    private String configValue;

    @Column("VALUE_TYPE")
    private String valueType;

    @Column("DESCRIPTION")
    private String description;

    @Column("IS_SENSITIVE")
    private Integer isSensitive;

    @Column("UPDATED_BY")
    private String updatedBy;

    @LastModifiedDate
    @Column("UPDATED_AT")
    private Instant updatedAt;
}