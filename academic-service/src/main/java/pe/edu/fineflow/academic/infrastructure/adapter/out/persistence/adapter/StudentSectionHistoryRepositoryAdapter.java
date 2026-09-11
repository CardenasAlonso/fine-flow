package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.academic.domain.model.StudentSectionHistory;
import pe.edu.fineflow.academic.domain.port.out.StudentSectionHistoryRepositoryPort;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.StudentSectionHistoryEntity;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository.StudentSectionHistoryR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StudentSectionHistoryRepositoryAdapter
        implements StudentSectionHistoryRepositoryPort {

    private final StudentSectionHistoryR2dbcRepository repository;

    @Override
    public Mono<StudentSectionHistory> save(StudentSectionHistory history) {
        return repository.save(toEntity(history)).map(this::toModel);
    }

    @Override
    public Flux<StudentSectionHistory> findByStudent(String schoolId, String studentId) {
        return repository
                .findBySchoolIdAndStudentIdOrderByChangedAtDesc(schoolId, studentId)
                .map(this::toModel);
    }

    @Override
    public Flux<StudentSectionHistory> findBySchool(String schoolId) {
        return repository
                .findBySchoolIdOrderByChangedAtDesc(schoolId)
                .map(this::toModel);
    }

    private StudentSectionHistoryEntity toEntity(StudentSectionHistory m) {
        StudentSectionHistoryEntity e = new StudentSectionHistoryEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setStudentId(m.getStudentId());
        e.setFromSectionId(m.getFromSectionId());
        e.setToSectionId(m.getToSectionId());
        e.setChangeReason(m.getChangeReason());
        e.setNotes(m.getNotes());
        e.setChangedBy(m.getChangedBy());
        e.setChangedAt(m.getChangedAt() != null ? m.getChangedAt() : Instant.now());
        e.setEffectiveDate(m.getEffectiveDate());
        return e;
    }

    private StudentSectionHistory toModel(StudentSectionHistoryEntity e) {
        StudentSectionHistory m = new StudentSectionHistory();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setStudentId(e.getStudentId());
        m.setFromSectionId(e.getFromSectionId());
        m.setToSectionId(e.getToSectionId());
        m.setChangeReason(e.getChangeReason());
        m.setNotes(e.getNotes());
        m.setChangedBy(e.getChangedBy());
        m.setChangedAt(e.getChangedAt());
        m.setEffectiveDate(e.getEffectiveDate());
        return m;
    }
}