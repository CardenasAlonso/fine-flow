package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageTimeSlotUseCase;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.academic.domain.port.out.TimeSlotRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageTimeSlotService extends BaseTenantService<TimeSlot> implements ManageTimeSlotUseCase {
    private final TimeSlotRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<TimeSlot> getRepository() {
        return repository;
    }

    @Override
    protected void applyUpdate(TimeSlot existing, TimeSlot updated) {
        existing.setSlotNumber(updated.getSlotNumber());
        existing.setSlotName(updated.getSlotName());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setDurationMin(updated.getDurationMin());
        existing.setSlotType(updated.getSlotType());
        existing.setIsActive(updated.getIsActive());
    }

    @Override
    protected String entityName() {
        return "TimeSlot";
    }

    @Override
    public Mono<TimeSlot> create(TimeSlot timeSlot) {
        timeSlot.setIsActive(1);
        return super.create(timeSlot);
    }

    @Override
    public Flux<TimeSlot> findAllActive(int offset, int limit) {
        return TenantContext.getSchoolId().flatMapMany(schoolId -> repository.findAllActiveBySchoolId(schoolId, offset, limit));
    }
}
