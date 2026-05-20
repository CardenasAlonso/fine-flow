package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.SchoolYear;
import pe.edu.fineflow.academic.domain.port.out.SchoolYearRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.SchoolYearMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.SchoolYearR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SchoolYearRepositoryAdapter implements SchoolYearRepositoryPort {
    private final SchoolYearR2dbcRepository repository;
    private final SchoolYearMapper mapper;

    @Override
    public Mono<SchoolYear> save(SchoolYear year) {
        return repository.save(mapper.toEntity(year)).map(mapper::toDomain);
    }

    @Override
    public Mono<SchoolYear> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<SchoolYear> findAllBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Flux<SchoolYear> findAllByAcademicLevelId(String academicLevelId) {
        return repository.findAllByAcademicLevelId(academicLevelId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
