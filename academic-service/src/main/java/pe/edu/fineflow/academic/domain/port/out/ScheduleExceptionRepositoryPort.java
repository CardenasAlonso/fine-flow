package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface ScheduleExceptionRepositoryPort extends BaseTenantRepositoryPort<ScheduleException> {
    Flux<ScheduleException> findAllByClassScheduleId(String classScheduleId);

    Flux<ScheduleException> findAllByDate(String schoolId, java.time.LocalDate date);
}
