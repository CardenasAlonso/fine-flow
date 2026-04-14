package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ScheduleExceptionEntity;

@Mapper(componentModel = "spring")
public interface ScheduleExceptionMapper {
    ScheduleExceptionEntity toEntity(ScheduleException exception);

    ScheduleException toDomain(ScheduleExceptionEntity entity);
}
