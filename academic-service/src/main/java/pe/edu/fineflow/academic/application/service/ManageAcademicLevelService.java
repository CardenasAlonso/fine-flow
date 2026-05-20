package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageAcademicLevelUseCase;
import pe.edu.fineflow.academic.domain.model.AcademicLevel;
import pe.edu.fineflow.academic.domain.port.out.AcademicLevelRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageAcademicLevelService extends BaseTenantService<AcademicLevel> implements ManageAcademicLevelUseCase {
    private final AcademicLevelRepositoryPort repository;

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
