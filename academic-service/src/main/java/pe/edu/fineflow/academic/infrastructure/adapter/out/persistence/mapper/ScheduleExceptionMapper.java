package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleExceptionEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleExceptionMapper {
    ScheduleExceptionEntity toEntity(ScheduleException exception);

    ScheduleException toDomain(ScheduleExceptionEntity entity);
}
