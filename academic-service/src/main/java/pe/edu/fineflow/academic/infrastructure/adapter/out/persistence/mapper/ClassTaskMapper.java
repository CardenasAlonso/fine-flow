package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.ClassTask;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassTaskEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassTaskMapper {
    ClassTaskEntity toEntity(ClassTask task);

    ClassTask toDomain(ClassTaskEntity entity);
}
