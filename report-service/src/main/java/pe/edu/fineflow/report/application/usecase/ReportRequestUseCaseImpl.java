package pe.edu.fineflow.report.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.report.application.port.in.RequestReportUseCase;
import pe.edu.fineflow.report.domain.model.ReportJob;
import pe.edu.fineflow.report.domain.port.out.ReportGeneratorPort;
import pe.edu.fineflow.report.domain.port.out.ReportJobRepositoryPort;
import pe.edu.fineflow.report.domain.port.out.ReportStoragePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ReportRequestUseCaseImpl implements RequestReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReportRequestUseCaseImpl.class);

    private final ReportJobRepositoryPort repo;
    private final ReportGeneratorPort generator;
    private final ReportStoragePort storage;

    public ReportRequestUseCaseImpl(
            ReportJobRepositoryPort repo,
            ReportGeneratorPort generator,
            ReportStoragePort storage) {
        this.repo = repo;
        this.generator = generator;
        this.storage = storage;
    }

    @Override
    public Mono<ReportJob> request(String reportType, String format, String parametersJson) {
        return TenantContext.getPrincipal()
                .flatMap(principal -> {
                    ReportJob job = ReportJob.create(
                            reportType, format, parametersJson, principal.schoolId(), principal.userId());
                    job.setId(UuidGenerator.generate());
                    return repo.save(job).doOnSuccess(saved -> processAsync(snapshot(saved)));
                });
    }

    private static ReportJob snapshot(ReportJob job) {
        ReportJob copy = new ReportJob();
        copy.setId(job.getId());
        copy.setSchoolId(job.getSchoolId());
        copy.setRequestedBy(job.getRequestedBy());
        copy.setReportType(job.getReportType());
        copy.setFormat(job.getFormat());
        copy.setParametersJson(job.getParametersJson());
        copy.setStatus(job.getStatus());
        copy.setFilePath(job.getFilePath());
        copy.setErrorDetail(job.getErrorDetail());
        copy.setFileSizeKb(job.getFileSizeKb());
        copy.setProgressPct(job.getProgressPct());
        copy.setRequestedAt(job.getRequestedAt());
        copy.setStartedAt(job.getStartedAt());
        copy.setCompletedAt(job.getCompletedAt());
        copy.setExpiresAt(job.getExpiresAt());
        copy.setDownloadCount(job.getDownloadCount());
        return copy;
    }

    public void processAsync(ReportJob job) {
        log.info("Starting async report generation: jobId={} type={}", job.getId(), job.getReportType());
        job.startProcessing();
        Mono.fromCallable(() -> generator.generate(job))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(bytes -> storage.store(job.getSchoolId(), job.getId(), job.getFormat(), bytes))
                .flatMap(path -> {
                    job.complete(path);
                    return repo.updateStatus(job.getId(), job.getStatus(), 100, path);
                })
                .doOnSuccess(v -> log.info("Report generated successfully: jobId={}", job.getId()))
                .doOnError(e -> {
                    log.error("Report generation failed for job {}: {}", job.getId(), e.getMessage());
                    job.fail(e.getMessage());
                    repo.updateStatus(job.getId(), job.getStatus(), 0, null)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                })
                .subscribe();
    }

    @Override
    public Mono<ReportJob> getStatus(String jobId) {
        return TenantContext.getSchoolId()
                .flatMap(sid -> repo.findByIdAndSchoolId(jobId, sid)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("ReportJob", jobId))));
    }

    @Override
    public Flux<ReportJob> myJobs() {
        return TenantContext.getPrincipal()
                .flatMapMany(p -> repo.findByRequestedByAndSchoolId(p.userId(), p.schoolId()));
    }

    @Override
    public Mono<DownloadResult> download(String jobId) {
        return getStatus(jobId)
                .flatMap(job -> {
                    if (job.getStatus() != ReportJob.Status.COMPLETED) {
                        return Mono.error(new IllegalStateException("El reporte aún no está listo."));
                    }
                    if (job.getFilePath() == null || job.getFilePath().isEmpty()) {
                        return Mono.error(new IllegalStateException("Ruta de archivo no disponible."));
                    }
                    return storage.load(job.getFilePath())
                            .map(bytes -> new DownloadResult(
                                    "report-" + jobId + "." + job.fileExtension(),
                                    job.contentType(),
                                    bytes));
                });
    }
}