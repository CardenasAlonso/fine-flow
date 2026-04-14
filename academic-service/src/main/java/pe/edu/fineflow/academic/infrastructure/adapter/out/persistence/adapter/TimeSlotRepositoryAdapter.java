package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.TimeSlot;
import pe.edu.fineflow.academic.domain.port.out.TimeSlotRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.TimeSlotMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.TimeSlotR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TimeSlotRepositoryAdapter implements TimeSlotRepositoryPort {
    private final TimeSlotR2dbcRepository repository;
    private final TimeSlotMapper mapper;

    @Override
    public Mono<TimeSlot> save(TimeSlot timeSlot) {
        return repository.save(mapper.toEntity(timeSlot)).map(mapper::toDomain);
    }

    @Override
    public Mono<TimeSlot> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<TimeSlot> findAllBySchoolId(String schoolId) {
        return repository.findAllBySchoolId(schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<TimeSlot> findAllActiveBySchoolId(String schoolId) {
        return repository
                .findAllBySchoolIdAndIsActiveOrderBySlotNumber(schoolId, 1)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
