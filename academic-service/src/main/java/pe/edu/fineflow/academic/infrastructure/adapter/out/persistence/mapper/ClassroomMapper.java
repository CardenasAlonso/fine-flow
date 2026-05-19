package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassroomEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassroomMapper {
    ClassroomEntity toEntity(Classroom classroom);

    Classroom toDomain(ClassroomEntity entity);
}
