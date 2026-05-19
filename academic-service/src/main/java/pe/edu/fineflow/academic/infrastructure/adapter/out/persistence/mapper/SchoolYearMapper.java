package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.SchoolYear;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.SchoolYearEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolYearMapper {
    SchoolYearEntity toEntity(SchoolYear year);

    SchoolYear toDomain(SchoolYearEntity entity);
}
