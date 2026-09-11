package pe.edu.fineflow.academic.application.port.in;

import pe.edu.fineflow.academic.domain.model.StudentSectionHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageStudentSectionHistoryUseCase {
    Mono<StudentSectionHistory> record(StudentSectionHistory history);

    Flux<StudentSectionHistory> findByStudent(String studentId);

    Flux<StudentSectionHistory> findBySchool();
}