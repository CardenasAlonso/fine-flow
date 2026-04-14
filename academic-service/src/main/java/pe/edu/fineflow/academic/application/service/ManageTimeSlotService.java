package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageTimeSlotUseCase;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.academic.domain.port.out.TimeSlotRepositoryPort;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageTimeSlotService implements ManageTimeSlotUseCase {
    private final TimeSlotRepositoryPort repository;

    @Override
    public Mono<TimeSlot> create(TimeSlot timeSlot) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId -> {
                            timeSlot.setId(UuidGenerator.generate());
                            timeSlot.setSchoolId(schoolId);
                            timeSlot.setIsActive(1);
                            return repository.save(timeSlot);
                        });
    }

    @Override
    public Mono<TimeSlot> update(String id, TimeSlot updated) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setSlotNumber(updated.getSlotNumber());
                            existing.setSlotName(updated.getSlotName());
                            existing.setStartTime(updated.getStartTime());
                            existing.setEndTime(updated.getEndTime());
                            existing.setDurationMin(updated.getDurationMin());
                            existing.setSlotType(updated.getSlotType());
                            existing.setIsActive(updated.getIsActive());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<TimeSlot> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<TimeSlot> findAll() {
        return TenantContext.getSchoolId().flatMapMany(repository::findAllBySchoolId);
    }

    @Override
    public Flux<TimeSlot> findAllActive() {
        return TenantContext.getSchoolId().flatMapMany(repository::findAllActiveBySchoolId);
    }
}
