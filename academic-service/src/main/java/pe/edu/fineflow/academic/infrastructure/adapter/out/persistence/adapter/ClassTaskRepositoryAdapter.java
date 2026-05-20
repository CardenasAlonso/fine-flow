package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.ClassTask;
import pe.edu.fineflow.academic.domain.port.out.ClassTaskRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.ClassTaskMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.ClassTaskR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClassTaskRepositoryAdapter implements ClassTaskRepositoryPort {
    private final ClassTaskR2dbcRepository repository;
    private final ClassTaskMapper mapper;

    @Override
    public Mono<ClassTask> save(ClassTask task) {
        return repository.save(mapper.toEntity(task)).map(mapper::toDomain);
    }

    @Override
    public Mono<ClassTask> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<ClassTask> findAllBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Flux<ClassTask> findAllByCourseAssignmentId(String courseAssignmentId) {
        return repository.findAllByCourseAssignmentId(courseAssignmentId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
