package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("CLASSROOMS")
public class ClassroomEntity {
    @Id
    @Column("ID")
    private String id;

    @Column("SCHOOL_ID")
    private String schoolId;

    @Column("NAME")
    private String name;

    @Column("ROOM_TYPE")
    private String roomType;

    @Column("CAPACITY")
    private Integer capacity;

    @Column("FLOOR_NUMBER")
    private Integer floorNumber;

    @Column("BUILDING")
    private String building;

    @Column("HAS_PROJECTOR")
    private Integer hasProjector;

    @Column("HAS_COMPUTERS")
    private Integer hasComputers;

    @Column("IS_ACTIVE")
    private Integer isActive;

    @Column("NOTES")
    private String notes;

    @Column("CREATED_AT")
    private Instant createdAt;
}
