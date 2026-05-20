package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.AcademicPeriod;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface AcademicPeriodRepositoryPort extends BaseTenantRepositoryPort<AcademicPeriod> {
    Flux<AcademicPeriod> findAllBySchoolYearId(String schoolYearId);
}
