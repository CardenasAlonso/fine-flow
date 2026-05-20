package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassScheduleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassScheduleMapper {
    ClassScheduleEntity toEntity(ClassSchedule classSchedule);

    ClassSchedule toDomain(ClassScheduleEntity entity);
}
