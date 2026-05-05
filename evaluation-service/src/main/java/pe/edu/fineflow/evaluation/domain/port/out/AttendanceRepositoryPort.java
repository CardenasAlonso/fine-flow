package pe.edu.fineflow.evaluation.domain.port.out;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.evaluation.domain.model.Attendance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AttendanceRepositoryPort {
    Mono<Attendance> save(Attendance a);

    Flux<Attendance> saveAll(List<Attendance> list);

    Mono<Attendance> findByIdAndSchoolId(String id, String schoolId);

    Flux<Attendance> findByStudentIdAndSchoolId(String studentId, String schoolId, Pageable pageable);

    Flux<Attendance> findByDateAndSchoolId(LocalDate date, String schoolId, Pageable pageable);

    Mono<Boolean> existsByStudentIdAndDateAndAssignment(
            String studentId, LocalDate date, String assignmentId, String schoolId);
}
