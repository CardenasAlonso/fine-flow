package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClassScheduleRepositoryPort {
    Mono<ClassSchedule> save(ClassSchedule classSchedule);

    Mono<ClassSchedule> findById(String id);

    Flux<ClassSchedule> findAllByScheduleVersionId(String scheduleVersionId);

    Flux<ClassSchedule> findByTeacherId(String teacherId);

    Flux<ClassSchedule> findBySectionId(String sectionId);

    Mono<Void> deleteById(String id);
}
