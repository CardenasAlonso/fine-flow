package pe.edu.fineflow.academic.application.service;

import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageAcademicPeriodUseCase;
import pe.edu.fineflow.academic.domain.model.AcademicPeriod;
import pe.edu.fineflow.academic.domain.port.out.AcademicPeriodRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Flux;

@Service
public class ManageAcademicPeriodService extends BaseTenantService<AcademicPeriod> implements ManageAcademicPeriodUseCase {
    private final AcademicPeriodRepositoryPort repository;

    public ManageAcademicPeriodService(AcademicPeriodRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    protected BaseTenantRepositoryPort<AcademicPeriod> getRepository() { return repository; }

    @Override
    protected String entityName() { return "AcademicPeriod"; }

    @Override
    protected void applyUpdate(AcademicPeriod existing, AcademicPeriod updated) {
        existing.setName(updated.getName());
        existing.setPeriodType(updated.getPeriodType());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    public Flux<AcademicPeriod> findAll(int offset, int limit) {
        return super.findAll(offset, limit);
    }

    @Override
    public Flux<AcademicPeriod> findBySchoolYear(String schoolYearId) {
        return repository.findAllBySchoolYearId(schoolYearId);
    }
}
