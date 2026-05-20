package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.ClassSchedule;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import reactor.core.publisher.Flux;

public interface ClassScheduleRepositoryPort extends BaseTenantRepositoryPort<ClassSchedule> {
    Flux<ClassSchedule> findAllByScheduleVersionId(String scheduleVersionId);

    Flux<ClassSchedule> findByTeacherId(String teacherId);

    Flux<ClassSchedule> findBySectionId(String sectionId);
}
