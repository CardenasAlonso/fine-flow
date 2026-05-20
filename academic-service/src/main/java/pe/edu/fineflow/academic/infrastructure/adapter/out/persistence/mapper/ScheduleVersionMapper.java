package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleVersionEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleVersionMapper {
    ScheduleVersionEntity toEntity(ScheduleVersion scheduleVersion);

    ScheduleVersion toDomain(ScheduleVersionEntity entity);
}
