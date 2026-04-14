package pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.evaluation.domain.model.Attendance;
import pe.edu.fineflow.evaluation.domain.port.out.AttendanceRepositoryPort;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.mapper.AttendancePersistenceMapper;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.repository.AttendanceR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AttendanceRepositoryAdapter implements AttendanceRepositoryPort {

    private final AttendanceR2dbcRepository repository;
    private final AttendancePersistenceMapper mapper;

    @Override
    public Mono<Attendance> save(Attendance attendance) {
        return repository.save(mapper.toEntity(attendance)).map(mapper::toDomain);
    }

    @Override
    public Flux<Attendance> saveAll(List<Attendance> list) {
        return Flux.fromIterable(list)
                .map(mapper::toEntity)
                .collectList()
                .flatMapMany(repository::saveAll)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Attendance> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Attendance> findByStudentIdAndSchoolId(String studentId, String schoolId) {
        return repository.findByStudentIdAndSchoolId(studentId, schoolId).map(mapper::toDomain);
    }

    @Override
    public Flux<Attendance> findByDateAndSchoolId(LocalDate date, String schoolId) {
        return repository.findByAttendanceDateAndSchoolId(date, schoolId).map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByStudentIdAndDateAndAssignment(
            String studentId, LocalDate date, String assignmentId, String schoolId) {
        return repository
                .countByStudentDateAssignment(studentId, date, assignmentId, schoolId)
                .map(count -> count > 0);
    }
}
