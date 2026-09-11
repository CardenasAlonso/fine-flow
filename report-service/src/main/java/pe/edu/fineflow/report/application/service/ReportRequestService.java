package pe.edu.fineflow.report.application.service;

import java.time.Instant;
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
public class ReportRequestService implements RequestReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReportRequestService.class);

    private final ReportJobRepositoryPort repo;
    private final ReportGeneratorPort generator;
    private final ReportStoragePort storage;

    public ReportRequestService(
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
                    ReportJob job = new ReportJob();
                    job.setId(UuidGenerator.generate());
                    job.setSchoolId(principal.schoolId());
                    job.setRequestedBy(principal.userId());
                    job.setReportType(reportType);
                    job.setFormat(format);
                    job.setParametersJson(parametersJson);
                    job.setStatus("PENDING");
                    job.setProgressPct(0);
                    job.setRequestedAt(Instant.now());
                    job.setExpiresAt(Instant.now().plusSeconds(72 * 3600));
                    return repo.save(job).doOnSuccess(this::processAsync);
                });
    }

    public void processAsync(ReportJob job) {
        log.info("Starting async report generation: jobId={} type={}", job.getId(), job.getReportType());
        Mono.fromCallable(() -> generator.generate(job))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(bytes -> storage.store(job.getSchoolId(), job.getId(), job.getFormat(), bytes))
                .flatMap(path -> repo.updateStatus(job.getId(), "PROCESSING", 10, null)
                        .then(repo.updateStatus(job.getId(), "COMPLETED", 100, path)))
                .doOnSuccess(v -> log.info("Report generated successfully: jobId={}", job.getId()))
                .doOnError(e -> {
                    log.error("Report generation failed for job {}: {}", job.getId(), e.getMessage());
                    repo.updateStatus(job.getId(), "FAILED", 0, null)
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
                    if (!"COMPLETED".equals(job.getStatus())) {
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