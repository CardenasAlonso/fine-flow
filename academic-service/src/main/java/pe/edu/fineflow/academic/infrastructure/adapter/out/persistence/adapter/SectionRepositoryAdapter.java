package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.Section;
import pe.edu.fineflow.academic.domain.port.out.SectionRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.SectionMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.SectionR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SectionRepositoryAdapter implements SectionRepositoryPort {
    private final SectionR2dbcRepository repository;
    private final SectionMapper mapper;

    @Override
    public Mono<Section> save(Section section) {
        return repository.save(mapper.toEntity(section)).map(mapper::toDomain);
    }

    @Override
    public Mono<Section> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Section> findAllBySchoolId(String schoolId) {
        return repository.findAllBySchoolId(schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Section> findAllActiveBySchoolId(String schoolId) {
        return repository.findAllActiveBySchoolId(schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Section> findAllBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Flux<Section> findAllActiveBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllActiveBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Flux<Section> findAllBySchoolYearId(String schoolYearId) {
        return repository.findAllBySchoolYearId(schoolYearId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
