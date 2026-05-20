package pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.evaluation.domain.model.Attendance;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.entity.AttendanceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttendancePersistenceMapper {
    AttendanceEntity toEntity(Attendance attendance);

    Attendance toDomain(AttendanceEntity entity);
}
