package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("TRANSLATIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("TRANS_KEY")
    private String transKey;

    @Column("LANG_CODE")
    private String langCode;

    @Column("VALUE")
    private String value;

    @Column("CONTEXT")
    private String context;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}