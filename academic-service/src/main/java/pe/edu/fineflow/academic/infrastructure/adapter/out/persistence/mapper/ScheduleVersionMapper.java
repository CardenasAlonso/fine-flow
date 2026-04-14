package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleVersionEntity;

@Mapper(componentModel = "spring")
public interface ScheduleVersionMapper {
    ScheduleVersionEntity toEntity(ScheduleVersion scheduleVersion);

    ScheduleVersion toDomain(ScheduleVersionEntity entity);
}
