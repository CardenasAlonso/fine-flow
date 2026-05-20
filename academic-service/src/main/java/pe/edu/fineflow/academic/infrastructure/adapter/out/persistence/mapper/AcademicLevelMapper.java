package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.AcademicLevel;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.AcademicLevelEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicLevelMapper {
    AcademicLevelEntity toEntity(AcademicLevel level);

    AcademicLevel toDomain(AcademicLevelEntity entity);
}
