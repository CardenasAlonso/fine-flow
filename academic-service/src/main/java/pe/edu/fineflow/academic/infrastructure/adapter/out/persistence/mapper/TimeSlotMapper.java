package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.TimeSlotEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimeSlotMapper {
    TimeSlotEntity toEntity(TimeSlot timeSlot);

    TimeSlot toDomain(TimeSlotEntity entity);
}
