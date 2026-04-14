package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.ScheduleVersion;
import pe.edu.fineflow.academic.domain.port.out.ScheduleVersionRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.ScheduleVersionMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.ScheduleVersionR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ScheduleVersionRepositoryAdapter implements ScheduleVersionRepositoryPort {
    private final ScheduleVersionR2dbcRepository repository;
    private final ScheduleVersionMapper mapper;

    @Override
    public Mono<ScheduleVersion> save(ScheduleVersion scheduleVersion) {
        return repository.save(mapper.toEntity(scheduleVersion)).map(mapper::toDomain);
    }

    @Override
    public Mono<ScheduleVersion> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<ScheduleVersion> findAllBySchoolId(String schoolId) {
        return repository.findAllBySchoolId(schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<ScheduleVersion> findActiveBySchoolId(String schoolId) {
        return repository.findAllBySchoolIdAndStatus(schoolId, "ACTIVE").map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
