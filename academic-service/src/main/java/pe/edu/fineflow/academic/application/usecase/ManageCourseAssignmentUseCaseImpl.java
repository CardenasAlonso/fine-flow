package pe.edu.fineflow.academic.application.usecase;

import pe.edu.fineflow.academic.application.port.in.ManageCourseAssignmentUseCase;
import pe.edu.fineflow.academic.domain.model.CourseAssignment;
import pe.edu.fineflow.academic.domain.port.out.CourseAssignmentRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ManageCourseAssignmentUseCaseImpl extends BaseTenantService<CourseAssignment> implements ManageCourseAssignmentUseCase {
    private final CourseAssignmentRepositoryPort repository;

    public ManageCourseAssignmentUseCaseImpl(CourseAssignmentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    protected BaseTenantRepositoryPort<CourseAssignment> getRepository() {
        return repository;
    }

    @Override
    protected void applyUpdate(CourseAssignment existing, CourseAssignment updated) {
        existing.setCourseId(updated.getCourseId());
        existing.setSectionId(updated.getSectionId());
        existing.setTeacherId(updated.getTeacherId());
        existing.setAcademicPeriodId(updated.getAcademicPeriodId());
        existing.setHoursPerWeek(updated.getHoursPerWeek());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    protected String entityName() {
        return "CourseAssignment";
    }

    @Override
    public Mono<CourseAssignment> create(CourseAssignment assignment) {
        assignment.setIsActive(1);
        return super.create(assignment);
    }

    @Override
    public Flux<CourseAssignment> findBySection(String sectionId) {
        return repository.findAllBySectionId(sectionId);
    }
}
