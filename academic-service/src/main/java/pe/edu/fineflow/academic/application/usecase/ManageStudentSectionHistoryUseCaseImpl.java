package pe.edu.fineflow.academic.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.academic.application.port.in.ManageStudentSectionHistoryUseCase;
import pe.edu.fineflow.academic.domain.model.StudentSectionHistory;
import pe.edu.fineflow.academic.domain.port.out.StudentSectionHistoryRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ManageStudentSectionHistoryUseCaseImpl implements ManageStudentSectionHistoryUseCase {

    private final StudentSectionHistoryRepositoryPort repo;

    public ManageStudentSectionHistoryUseCaseImpl(StudentSectionHistoryRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<StudentSectionHistory> record(StudentSectionHistory history) {
        return TenantContext.getPrincipal()
                .flatMap(
                        p -> {
                            if (history.getId() == null) {
                                history.setId(UuidGenerator.generate());
                            }
                            history.setSchoolId(p.schoolId());
                            history.setChangedBy(p.userId());
                            history.setChangedAt(Instant.now());
                            return repo.save(history);
                        });
    }

    @Override
    public Flux<StudentSectionHistory> findByStudent(String studentId) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findByStudent(sid, studentId));
    }

    @Override
    public Flux<StudentSectionHistory> findBySchool() {
        return TenantContext.getSchoolId().flatMapMany(repo::findBySchool);
    }
}
