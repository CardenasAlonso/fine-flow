package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageSchoolYearUseCase;
import pe.edu.fineflow.academic.domain.model.SchoolYear;
import pe.edu.fineflow.academic.domain.port.out.SchoolYearRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageSchoolYearService extends BaseTenantService<SchoolYear> implements ManageSchoolYearUseCase {
    private final SchoolYearRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<SchoolYear> getRepository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Año Escolar";
    }

    @Override
    protected void applyUpdate(SchoolYear existing, SchoolYear updated) {
        existing.setName(updated.getName());
        existing.setGradeNumber(updated.getGradeNumber());
        existing.setCalendarYear(updated.getCalendarYear());
        existing.setAcademicLevelId(updated.getAcademicLevelId());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    public Mono<SchoolYear> create(SchoolYear year) {
        year.setIsActive(1);
        return super.create(year);
    }

    @Override
    public Flux<SchoolYear> findByAcademicLevel(String academicLevelId) {
        return repository.findAllByAcademicLevelId(academicLevelId);
    }
}
