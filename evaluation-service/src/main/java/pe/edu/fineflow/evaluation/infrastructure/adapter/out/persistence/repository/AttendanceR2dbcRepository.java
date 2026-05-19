package pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.repository;

import java.time.LocalDate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.entity.AttendanceEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AttendanceR2dbcRepository extends R2dbcRepository<AttendanceEntity, String> {
    Mono<AttendanceEntity> findByIdAndSchoolId(String id, String schoolId);

    Flux<AttendanceEntity> findByStudentIdAndSchoolId(String studentId, String schoolId);

    Flux<AttendanceEntity> findByAttendanceDateAndSchoolId(LocalDate date, String schoolId);

    @Query(
            "SELECT * FROM ATTENDANCES WHERE student_id = :studentId AND school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AttendanceEntity> findByStudentIdAndSchoolId(String studentId, String schoolId, int offset, int limit);

    @Query(
            "SELECT * FROM ATTENDANCES WHERE attendance_date = :date AND school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AttendanceEntity> findByAttendanceDateAndSchoolId(LocalDate date, String schoolId, int offset, int limit);

    @Query(
            "SELECT COUNT(*) FROM ATTENDANCES WHERE student_id = :studentId AND attendance_date = :date"
                + " AND (course_assignment_id = :assignmentId OR (course_assignment_id IS NULL AND"
                + " :assignmentId IS NULL)) AND school_id = :schoolId")
    Mono<Long> countByStudentDateAssignment(
            String studentId, LocalDate date, String assignmentId, String schoolId);
}
