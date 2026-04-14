package pe.edu.fineflow.evaluation.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pe.edu.fineflow.evaluation.domain.model.Attendance;
import pe.edu.fineflow.evaluation.infrastructure.adapter.in.web.dto.AttendanceDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AttendanceRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "justificationReason", ignore = true)
    @Mapping(target = "registeredBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Attendance toDomain(AttendanceDto.Create dto);
}
