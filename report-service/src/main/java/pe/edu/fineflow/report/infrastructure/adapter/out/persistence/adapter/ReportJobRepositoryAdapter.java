package pe.edu.fineflow.report.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.report.domain.model.ReportJob;
import pe.edu.fineflow.report.domain.port.out.ReportJobRepositoryPort;
import pe.edu.fineflow.report.infrastructure.adapter.out.persistence.entity.ReportJobEntity;
import pe.edu.fineflow.report.infrastructure.adapter.out.persistence.repository.ReportJobR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReportJobRepositoryAdapter implements ReportJobRepositoryPort {

    private final ReportJobR2dbcRepository repository;

    @Override
    public Mono<ReportJob> save(ReportJob job) {
        return repository.save(toEntity(job)).map(this::toModel);
    }

    @Override
    public Mono<ReportJob> findByIdAndSchoolId(String id, String schoolId) {
        return repository.findByIdAndSchoolId(id, schoolId).map(this::toModel);
    }

    @Override
    public Flux<ReportJob> findByRequestedByAndSchoolId(String userId, String schoolId) {
        return repository.findByRequestedByAndSchoolId(userId, schoolId).map(this::toModel);
    }

    @Override
    public Flux<ReportJob> findPending() {
        return repository.findByStatus("PENDING").map(this::toModel);
    }

    @Override
    public Mono<Void> updateStatus(String id, ReportJob.Status status, int progress, String filePath) {
        return repository
                .findById(id)
                .flatMap(
                        e -> {
                            e.setStatus(status.name());
                            e.setProgressPct(progress);
                            e.setFilePath(filePath);
                            return repository.save(e);
                        })
                .then();
    }

    private ReportJobEntity toEntity(ReportJob m) {
        ReportJobEntity e = new ReportJobEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setRequestedBy(m.getRequestedBy());
        e.setReportType(m.getReportType());
        e.setFormat(m.getFormat());
        e.setParametersJson(m.getParametersJson());
        e.setStatus(m.getStatus() != null ? m.getStatus().name() : null);
        e.setFilePath(m.getFilePath());
        e.setErrorDetail(m.getErrorDetail());
        e.setFileSizeKb(m.getFileSizeKb());
        e.setProgressPct(m.getProgressPct());
        e.setRequestedAt(m.getRequestedAt());
        e.setStartedAt(m.getStartedAt());
        e.setCompletedAt(m.getCompletedAt());
        e.setExpiresAt(m.getExpiresAt());
        e.setDownloadCount(m.getDownloadCount());
        return e;
    }

    private ReportJob toModel(ReportJobEntity e) {
        ReportJob m = new ReportJob();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setRequestedBy(e.getRequestedBy());
        m.setReportType(e.getReportType());
        m.setFormat(e.getFormat());
        m.setParametersJson(e.getParametersJson());
        m.setStatus(e.getStatus() != null ? ReportJob.Status.valueOf(e.getStatus()) : null);
        m.setFilePath(e.getFilePath());
        m.setErrorDetail(e.getErrorDetail());
        m.setFileSizeKb(e.getFileSizeKb());
        m.setProgressPct(e.getProgressPct());
        m.setRequestedAt(e.getRequestedAt());
        m.setStartedAt(e.getStartedAt());
        m.setCompletedAt(e.getCompletedAt());
        m.setExpiresAt(e.getExpiresAt());
        m.setDownloadCount(e.getDownloadCount());
        return m;
    }
}
