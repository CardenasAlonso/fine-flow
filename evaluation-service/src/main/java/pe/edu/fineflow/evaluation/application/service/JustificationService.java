package pe.edu.fineflow.evaluation.application.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.evaluation.application.port.in.ManageJustificationUseCase;
import pe.edu.fineflow.evaluation.domain.model.Justification;
import pe.edu.fineflow.evaluation.domain.port.out.JustificationRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JustificationService implements ManageJustificationUseCase {

    private final JustificationRepositoryPort repo;

    public JustificationService(JustificationRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Justification> request(Justification justification) {
        return TenantContext.getPrincipal()
                .flatMap(
                        p -> {
                            if (justification.getId() == null) {
                                justification.setId(UuidGenerator.generate());
                            }
                            justification.setSchoolId(p.schoolId());
                            justification.setRequestedBy(p.userId());
                            justification.setStatus("PENDING");
                            justification.setAutoApproved(0);
                            justification.setRequestedAt(Instant.now());
                            justification.setCreatedAt(Instant.now());
                            return repo.save(justification);
                        });
    }

    @Override
    public Mono<Justification> review(String id, String decision, String reviewNote) {
        return TenantContext.getPrincipal()
                .flatMap(
                        p -> repo.findById(id)
                                .filter(j -> j.getSchoolId().equals(p.schoolId()))
                                .switchIfEmpty(
                                        Mono.error(
                                                new pe.edu.fineflow.common.exception
                                                        .ResourceNotFoundException(
                                                        "Justification", id)))
                                .flatMap(
                                        j -> {
                                            j.setStatus(decision);
                                            j.setReviewedBy(p.userId());
                                            j.setReviewNote(reviewNote);
                                            j.setReviewedAt(Instant.now());
                                            return repo.save(j);
                                        }));
    }

    @Override
    public Flux<Justification> findByStudent(String studentId) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findByStudent(sid, studentId));
    }

    @Override
    public Flux<Justification> findByStatus(String status) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findByStatus(sid, status));
    }
}