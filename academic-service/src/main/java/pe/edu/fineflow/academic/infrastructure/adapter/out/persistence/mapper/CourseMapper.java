package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pe.edu.fineflow.academic.domain.model.Course;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.CourseEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    CourseEntity toEntity(Course course);

    Course toDomain(CourseEntity entity);
}
