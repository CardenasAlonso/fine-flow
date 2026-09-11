package pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.evaluation.domain.model.Justification;
import pe.edu.fineflow.evaluation.domain.port.out.JustificationRepositoryPort;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.entity.JustificationEntity;
import pe.edu.fineflow.evaluation.infrastructure.adapter.out.persistence.repository.JustificationR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JustificationRepositoryAdapter implements JustificationRepositoryPort {

    private final JustificationR2dbcRepository repository;

    @Override
    public Mono<Justification> save(Justification justification) {
        return repository.save(toEntity(justification)).map(this::toModel);
    }

    @Override
    public Mono<Justification> findById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public Flux<Justification> findByStudent(String schoolId, String studentId) {
        return repository
                .findBySchoolIdAndStudentIdOrderByRequestedAtDesc(schoolId, studentId)
                .map(this::toModel);
    }

    @Override
    public Flux<Justification> findByStatus(String schoolId, String status) {
        return repository
                .findBySchoolIdAndStatusOrderByRequestedAtAsc(schoolId, status)
                .map(this::toModel);
    }

    private JustificationEntity toEntity(Justification m) {
        JustificationEntity e = new JustificationEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setStudentId(m.getStudentId());
        e.setAttendanceId(m.getAttendanceId());
        e.setRequestedBy(m.getRequestedBy());
        e.setReason(m.getReason());
        e.setDocumentUrl(m.getDocumentUrl());
        e.setStatus(m.getStatus());
        e.setReviewedBy(m.getReviewedBy());
        e.setReviewNote(m.getReviewNote());
        e.setAutoApproved(m.getAutoApproved() != null && m.getAutoApproved() == 1 ? 1 : 0);
        e.setRequestedAt(m.getRequestedAt() != null ? m.getRequestedAt() : Instant.now());
        e.setReviewedAt(m.getReviewedAt());
        e.setExpiresAt(m.getExpiresAt());
        return e;
    }

    private Justification toModel(JustificationEntity e) {
        Justification m = new Justification();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setStudentId(e.getStudentId());
        m.setAttendanceId(e.getAttendanceId());
        m.setRequestedBy(e.getRequestedBy());
        m.setReason(e.getReason());
        m.setDocumentUrl(e.getDocumentUrl());
        m.setStatus(e.getStatus());
        m.setReviewedBy(e.getReviewedBy());
        m.setReviewNote(e.getReviewNote());
        m.setAutoApproved(e.getAutoApproved());
        m.setRequestedAt(e.getRequestedAt());
        m.setReviewedAt(e.getReviewedAt());
        m.setExpiresAt(e.getExpiresAt());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}