package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface TimeSlotRepositoryPort extends BaseTenantRepositoryPort<TimeSlot> {
    Flux<TimeSlot> findAllBySchoolId(String schoolId);

    Flux<TimeSlot> findAllActiveBySchoolId(String schoolId);

    Flux<TimeSlot> findAllActiveBySchoolId(String schoolId, int offset, int limit);
}
