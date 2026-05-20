package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.AcademicLevel;
import pe.edu.fineflow.academic.domain.port.out.AcademicLevelRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.AcademicLevelMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.AcademicLevelR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AcademicLevelRepositoryAdapter implements AcademicLevelRepositoryPort {
    private final AcademicLevelR2dbcRepository repository;
    private final AcademicLevelMapper mapper;

    @Override
    public Mono<AcademicLevel> save(AcademicLevel level) {
        return repository.save(mapper.toEntity(level)).map(mapper::toDomain);
    }

    @Override
    public Mono<AcademicLevel> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<AcademicLevel> findAllBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
