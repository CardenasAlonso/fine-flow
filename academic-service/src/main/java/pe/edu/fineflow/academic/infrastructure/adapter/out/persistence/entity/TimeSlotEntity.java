package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("TIME_SLOTS")
public class TimeSlotEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

    @Column("SLOT_NUMBER")
    private Integer slotNumber;

    @Column("SLOT_NAME")
    private String slotName;

    @Column("START_TIME")
    private String startTime;

    @Column("END_TIME")
    private String endTime;

    @Column("DURATION_MIN")
    private Integer durationMin;

    @Column("SLOT_TYPE")
    private String slotType;

    @Column("IS_ACTIVE")
    private Integer isActive;
}
