package pe.edu.fineflow.evaluation.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pe.edu.fineflow.evaluation.domain.model.StudentScore;
import pe.edu.fineflow.evaluation.infrastructure.adapter.in.web.dto.ScoreDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScoreRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "registeredBy", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentScore toDomain(ScoreDto.Create dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "classTaskId", ignore = true)
    @Mapping(target = "registeredBy", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentScore toDomainUpdate(ScoreDto.Update dto);
}
