package pe.edu.fineflow.academic.application.usecase;

import pe.edu.fineflow.academic.application.port.in.ManageAcademicLevelUseCase;
import pe.edu.fineflow.academic.domain.model.AcademicLevel;
import pe.edu.fineflow.academic.domain.port.out.AcademicLevelRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Mono;

public class ManageAcademicLevelUseCaseImpl extends BaseTenantService<AcademicLevel> implements ManageAcademicLevelUseCase {
    private final AcademicLevelRepositoryPort repository;

    public ManageAcademicLevelUseCaseImpl(AcademicLevelRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    protected BaseTenantRepositoryPort<AcademicLevel> getRepository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Nivel Académico";
    }

    @Override
    protected void applyUpdate(AcademicLevel existing, AcademicLevel updated) {
        existing.setName(updated.getName());
        existing.setOrderNum(updated.getOrderNum());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    public Mono<AcademicLevel> create(AcademicLevel level) {
        level.setIsActive(1);
        return super.create(level);
    }
}
