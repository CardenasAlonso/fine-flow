package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.Section;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.SectionEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectionMapper {
    SectionEntity toEntity(Section section);

    Section toDomain(SectionEntity entity);
}
