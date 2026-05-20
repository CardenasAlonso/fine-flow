package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.fineflow.academic.application.port.in.ManageSectionUseCase;
import pe.edu.fineflow.academic.domain.model.Section;
import pe.edu.fineflow.academic.domain.port.out.SectionRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageSectionService extends BaseTenantService<Section> implements ManageSectionUseCase {
    private final SectionRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<Section> getRepository() {
        return repository;
    }

    @Override
    protected void applyUpdate(Section existing, Section updated) {
        existing.setName(updated.getName());
        existing.setMaxCapacity(updated.getMaxCapacity());
        existing.setTutorId(updated.getTutorId());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    protected String entityName() {
        return "Section";
    }

    @Override
    @Transactional
    public Mono<Section> create(Section section) {
        section.setIsActive(1);
        return super.create(section);
    }

    @Override
    public Flux<Section> findAllActive(int offset, int limit) {
        return TenantContext.getSchoolId().flatMapMany(schoolId -> repository.findAllActiveBySchoolId(schoolId, offset, limit));
    }

    @Override
    public Flux<Section> findBySchoolYear(String schoolYearId) {
        return repository.findAllBySchoolYearId(schoolYearId);
    }
}
