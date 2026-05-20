package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.AcademicPeriod;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.AcademicPeriodEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicPeriodMapper {
    AcademicPeriodEntity toEntity(AcademicPeriod period);

    AcademicPeriod toDomain(AcademicPeriodEntity entity);
}
