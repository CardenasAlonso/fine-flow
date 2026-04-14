package pe.edu.fineflow.academic.application.service;

import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageScheduleExceptionUseCase;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.academic.domain.port.out.ScheduleExceptionRepositoryPort;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageScheduleExceptionService implements ManageScheduleExceptionUseCase {
    private final ScheduleExceptionRepositoryPort repository;

    @Override
    public Mono<ScheduleException> create(ScheduleException exception) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId -> {
                            exception.setId(UuidGenerator.generate());
                            exception.setSchoolId(schoolId);
                            exception.setCreatedAt(Instant.now());
                            return repository.save(exception);
                        });
    }

    @Override
    public Mono<ScheduleException> update(String id, ScheduleException updated) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setExceptionType(updated.getExceptionType());
                            existing.setSubstituteTeacherId(updated.getSubstituteTeacherId());
                            existing.setSubstituteClassroomId(updated.getSubstituteClassroomId());
                            existing.setSubstituteSlotId(updated.getSubstituteSlotId());
                            existing.setReason(updated.getReason());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<ScheduleException> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<ScheduleException> findByClassSchedule(String classScheduleId) {
        return repository.findAllByClassScheduleId(classScheduleId);
    }

    @Override
    public Flux<ScheduleException> findByDate(LocalDate date) {
        return TenantContext.getSchoolId()
                .flatMapMany(schoolId -> repository.findAllByDate(schoolId, date));
    }
}
