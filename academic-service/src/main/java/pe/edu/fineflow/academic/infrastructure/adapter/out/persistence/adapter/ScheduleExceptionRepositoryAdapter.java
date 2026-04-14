package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.ScheduleException;
import pe.edu.fineflow.academic.domain.port.out.ScheduleExceptionRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.ScheduleExceptionMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.ScheduleExceptionR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ScheduleExceptionRepositoryAdapter implements ScheduleExceptionRepositoryPort {
    private final ScheduleExceptionR2dbcRepository repository;
    private final ScheduleExceptionMapper mapper;

    @Override
    public Mono<ScheduleException> save(ScheduleException exception) {
        return repository.save(mapper.toEntity(exception)).map(mapper::toDomain);
    }

    @Override
    public Mono<ScheduleException> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<ScheduleException> findAllByClassScheduleId(String classScheduleId) {
        return repository
                .findAllByClassScheduleIdAndIsActive(classScheduleId, 1)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<ScheduleException> findAllByDate(String schoolId, LocalDate date) {
        return repository
                .findAllBySchoolIdAndExceptionDateAndIsActive(schoolId, date, 1)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
