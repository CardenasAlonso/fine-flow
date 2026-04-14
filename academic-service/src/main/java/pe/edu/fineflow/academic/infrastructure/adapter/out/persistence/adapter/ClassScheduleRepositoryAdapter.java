package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import pe.edu.fineflow.academic.domain.port.out.ClassScheduleRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.mapper.ClassScheduleMapper;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.ClassScheduleR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClassScheduleRepositoryAdapter implements ClassScheduleRepositoryPort {
    private final ClassScheduleR2dbcRepository repository;
    private final ClassScheduleMapper mapper;

    @Override
    public Mono<ClassSchedule> save(ClassSchedule classSchedule) {
        return repository.save(mapper.toEntity(classSchedule)).map(mapper::toDomain);
    }

    @Override
    public Mono<ClassSchedule> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<ClassSchedule> findAllByScheduleVersionId(String scheduleVersionId) {
        return repository.findAllByScheduleVersionId(scheduleVersionId).map(mapper::toDomain);
    }

    @Override
    public Flux<ClassSchedule> findByTeacherId(String teacherId) {
        return repository.findAllByTeacherId(teacherId).map(mapper::toDomain);
    }

    @Override
    public Flux<ClassSchedule> findBySectionId(String sectionId) {
        return repository.findAllBySectionId(sectionId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
