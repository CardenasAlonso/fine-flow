package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.academic.domain.port.out.ClassroomRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.ClassroomMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.ClassroomR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClassroomRepositoryAdapter implements ClassroomRepositoryPort {
    private final ClassroomR2dbcRepository repository;
    private final ClassroomMapper mapper;

    @Override
    public Mono<Classroom> save(Classroom classroom) {
        return repository.save(mapper.toEntity(classroom)).map(mapper::toDomain);
    }

    @Override
    public Mono<Classroom> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Classroom> findAllBySchoolId(String schoolId) {
        return repository.findAllBySchoolId(schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Classroom> findAllActiveBySchoolId(String schoolId) {
        return repository.findAllBySchoolIdAndIsActive(schoolId, 1).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
