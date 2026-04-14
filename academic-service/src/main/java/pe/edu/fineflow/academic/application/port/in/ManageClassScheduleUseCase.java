package pe.edu.fineflow.academic.application.port.in;

import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageClassScheduleUseCase {
    Mono<ClassSchedule> create(ClassSchedule classSchedule);

    Mono<ClassSchedule> update(String id, ClassSchedule classSchedule);

    Mono<Void> delete(String id);

    Mono<ClassSchedule> findById(String id);

    Flux<ClassSchedule> findByScheduleVersion(String scheduleVersionId);

    Flux<ClassSchedule> findByTeacher(String teacherId);

    Flux<ClassSchedule> findBySection(String sectionId);
}
