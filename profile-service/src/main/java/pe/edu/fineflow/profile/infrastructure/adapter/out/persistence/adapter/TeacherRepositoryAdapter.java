package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.profile.domain.model.Teacher;
import pe.edu.fineflow.profile.domain.port.out.TeacherRepositoryPort;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper.TeacherPersistenceMapper;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.repository.TeacherR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TeacherRepositoryAdapter implements TeacherRepositoryPort {

    private final TeacherR2dbcRepository repository;
    private final TeacherPersistenceMapper mapper;

    @Override
    public Mono<Teacher> save(Teacher teacher) {
        return repository.save(mapper.toEntity(teacher)).map(mapper::toDomain);
    }

    @Override
    public Mono<Teacher> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Teacher> findAllBySchoolId(String schoolId) {
        return repository.findAllBySchoolId(schoolId).map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByDocumentNumberAndSchoolId(String doc, String schoolId) {
        return repository.existsByDocumentNumberAndSchoolId(doc, schoolId);
    }

    @Override
    public Mono<Void> deleteByIdAndSchoolId(String id, String schoolId) {
        return repository.deleteByIdAndSchoolId(id, schoolId);
    }
}
