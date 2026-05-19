package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.profile.domain.model.Student;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.StudentEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentPersistenceMapper {
    Student toDomain(StudentEntity entity);

    StudentEntity toEntity(Student domain);
}
