package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.profile.domain.model.Teacher;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.TeacherEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherPersistenceMapper {
    TeacherEntity toEntity(Teacher teacher);

    Teacher toDomain(TeacherEntity entity);
}
