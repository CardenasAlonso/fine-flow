package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.ClassroomEntity;

@Mapper(componentModel = "spring")
public interface ClassroomMapper {
    ClassroomEntity toEntity(Classroom classroom);

    Classroom toDomain(ClassroomEntity entity);
}
