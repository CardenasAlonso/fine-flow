package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.profile.domain.model.Guardian;
import pe.edu.fineflow.profile.domain.port.out.GuardianRepositoryPort;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper.GuardianPersistenceMapper;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.repository.GuardianR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GuardianRepositoryAdapter implements GuardianRepositoryPort {
    private final GuardianR2dbcRepository repository;
    private final GuardianPersistenceMapper mapper;

    @Override
    public Mono<Guardian> save(Guardian guardian) {
        return repository.save(mapper.toEntity(guardian)).map(mapper::toDomain);
    }

    @Override
    public Mono<Guardian> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Guardian> findAllBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Flux<Guardian> findAllByStudentId(String studentId) {
        return repository.findAllByStudentId(studentId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
