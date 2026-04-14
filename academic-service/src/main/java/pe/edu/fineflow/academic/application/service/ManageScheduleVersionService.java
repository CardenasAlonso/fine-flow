package pe.edu.fineflow.academic.application.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageScheduleVersionUseCase;
import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import pe.edu.fineflow.academic.domain.port.out.ScheduleVersionRepositoryPort;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageScheduleVersionService implements ManageScheduleVersionUseCase {
    private final ScheduleVersionRepositoryPort repository;

    @Override
    public Mono<ScheduleVersion> create(ScheduleVersion scheduleVersion) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId -> {
                            scheduleVersion.setId(UuidGenerator.generate());
                            scheduleVersion.setSchoolId(schoolId);
                            scheduleVersion.setStatus("DRAFT");
                            scheduleVersion.setCreatedAt(Instant.now());
                            scheduleVersion.setUpdatedAt(Instant.now());
                            return repository.save(scheduleVersion);
                        });
    }

    @Override
    public Mono<ScheduleVersion> update(String id, ScheduleVersion updated) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setVersionName(updated.getVersionName());
                            existing.setAcademicPeriodId(updated.getAcademicPeriodId());
                            existing.setNotes(updated.getNotes());
                            existing.setValidFrom(updated.getValidFrom());
                            existing.setValidUntil(updated.getValidUntil());
                            existing.setUpdatedAt(Instant.now());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<ScheduleVersion> publish(String id) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setStatus("ACTIVE");
                            existing.setPublishedAt(Instant.now());
                            existing.setUpdatedAt(Instant.now());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<ScheduleVersion> archive(String id) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setStatus("ARCHIVED");
                            existing.setUpdatedAt(Instant.now());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<ScheduleVersion> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<ScheduleVersion> findAll() {
        return TenantContext.getSchoolId().flatMapMany(repository::findAllBySchoolId);
    }

    @Override
    public Flux<ScheduleVersion> findActive() {
        return TenantContext.getSchoolId().flatMapMany(repository::findActiveBySchoolId);
    }
}
