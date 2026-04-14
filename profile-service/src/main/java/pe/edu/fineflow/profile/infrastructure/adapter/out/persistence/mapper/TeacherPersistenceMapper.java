package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import pe.edu.fineflow.profile.domain.model.Teacher;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.TeacherEntity;

@Mapper(componentModel = "spring")
public interface TeacherPersistenceMapper {
    TeacherEntity toEntity(Teacher teacher);

    Teacher toDomain(TeacherEntity entity);
}
