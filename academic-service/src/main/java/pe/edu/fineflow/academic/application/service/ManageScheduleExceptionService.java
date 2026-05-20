package pe.edu.fineflow.academic.application.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageScheduleExceptionUseCase;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.academic.domain.port.out.ScheduleExceptionRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageScheduleExceptionService extends BaseTenantService<ScheduleException> implements ManageScheduleExceptionUseCase {
    private final ScheduleExceptionRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<ScheduleException> getRepository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "ScheduleException";
    }

    @Override
    protected void applyUpdate(ScheduleException existing, ScheduleException updated) {
        existing.setExceptionType(updated.getExceptionType());
        existing.setSubstituteTeacherId(updated.getSubstituteTeacherId());
        existing.setSubstituteClassroomId(updated.getSubstituteClassroomId());
        existing.setSubstituteSlotId(updated.getSubstituteSlotId());
        existing.setReason(updated.getReason());
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
