package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.StudentSectionHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentSectionHistoryRepositoryPort {
    Mono<StudentSectionHistory> save(StudentSectionHistory history);

    Flux<StudentSectionHistory> findByStudent(String schoolId, String studentId);

    Flux<StudentSectionHistory> findBySchool(String schoolId);
}