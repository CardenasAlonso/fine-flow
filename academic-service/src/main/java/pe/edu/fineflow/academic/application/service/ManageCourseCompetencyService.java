package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageCourseCompetencyUseCase;
import pe.edu.fineflow.academic.domain.model.CourseCompetency;
import pe.edu.fineflow.academic.domain.port.out.CourseCompetencyRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageCourseCompetencyService extends BaseTenantService<CourseCompetency> implements ManageCourseCompetencyUseCase {
    private final CourseCompetencyRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<CourseCompetency> getRepository() {
        return repository;
    }

    @Override
    protected void applyUpdate(CourseCompetency existing, CourseCompetency updated) {
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setWeight(updated.getWeight());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    protected String entityName() {
        return "CourseCompetency";
    }

    @Override
    public Mono<CourseCompetency> create(CourseCompetency competency) {
        competency.setIsActive(1);
        return super.create(competency);
    }

    @Override
    public Flux<CourseCompetency> findByCourse(String courseId) {
        return repository.findAllByCourseId(courseId);
    }
}
