package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.SchoolYear;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface SchoolYearRepositoryPort extends BaseTenantRepositoryPort<SchoolYear> {
    Flux<SchoolYear> findAllByAcademicLevelId(String academicLevelId);
}
