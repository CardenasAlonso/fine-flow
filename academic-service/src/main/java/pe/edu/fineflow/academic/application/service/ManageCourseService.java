package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageCourseUseCase;
import pe.edu.fineflow.academic.domain.model.Course;
import pe.edu.fineflow.academic.domain.port.out.CourseRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageCourseService extends BaseTenantService<Course> implements ManageCourseUseCase {
    private final CourseRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<Course> getRepository() {
        return repository;
    }

    @Override
    protected void applyUpdate(Course existing, Course updated) {
        existing.setName(updated.getName());
        existing.setCode(updated.getCode());
        existing.setDescription(updated.getDescription());
        existing.setColorHex(updated.getColorHex());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    protected String entityName() {
        return "Course";
    }

    @Override
    public Mono<Course> create(Course course) {
        course.setIsActive(1);
        return super.create(course);
    }
}
