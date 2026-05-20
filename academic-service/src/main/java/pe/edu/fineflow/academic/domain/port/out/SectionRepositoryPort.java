package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.Section;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface SectionRepositoryPort extends BaseTenantRepositoryPort<Section> {
    Flux<Section> findAllBySchoolId(String schoolId);

    Flux<Section> findAllActiveBySchoolId(String schoolId);

    Flux<Section> findAllActiveBySchoolId(String schoolId, int offset, int limit);

    Flux<Section> findAllBySchoolYearId(String schoolYearId);
}
