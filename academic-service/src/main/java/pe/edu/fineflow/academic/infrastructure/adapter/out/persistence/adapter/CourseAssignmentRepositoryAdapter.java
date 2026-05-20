package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.CourseAssignment;
import pe.edu.fineflow.academic.domain.port.out.CourseAssignmentRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.CourseAssignmentMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.CourseAssignmentR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CourseAssignmentRepositoryAdapter implements CourseAssignmentRepositoryPort {
    private final CourseAssignmentR2dbcRepository repository;
    private final CourseAssignmentMapper mapper;

    @Override
    public Mono<CourseAssignment> save(CourseAssignment assignment) {
        return repository.save(mapper.toEntity(assignment)).map(mapper::toDomain);
    }

    @Override
    public Mono<CourseAssignment> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<CourseAssignment> findAllBySchoolId(String schoolId, int offset, int limit) {
        return repository.findAllBySchoolId(schoolId, offset, limit).map(mapper::toDomain);
    }

    @Override
    public Flux<CourseAssignment> findAllBySectionId(String sectionId) {
        return repository.findAllBySectionId(sectionId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
