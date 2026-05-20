package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface ScheduleVersionRepositoryPort extends BaseTenantRepositoryPort<ScheduleVersion> {
    Flux<ScheduleVersion> findAllBySchoolId(String schoolId);

    Flux<ScheduleVersion> findActiveBySchoolId(String schoolId);
}
