package pe.edu.fineflow.report.application.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.report.application.port.in.RequestReportUseCase;
import pe.edu.fineflow.report.domain.model.ReportJob;
import pe.edu.fineflow.report.domain.port.out.ReportJobRepositoryPort;
import pe.edu.fineflow.report.infrastructure.generator.ExcelReportGenerator;
import pe.edu.fineflow.report.infrastructure.generator.PdfReportGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class ReportRequestService implements RequestReportUseCase {

    private final ReportJobRepositoryPort repo;
    private final PdfReportGenerator pdfGen;
    private final ExcelReportGenerator excelGen;
    private final String outputDir;

    public ReportRequestService(
            ReportJobRepositoryPort repo,
            PdfReportGenerator pdfGen,
            ExcelReportGenerator excelGen,
            @Value("${fineflow.reports.output-dir:/reports}") String outputDir) {
        this.repo = repo;
        this.pdfGen = pdfGen;
        this.excelGen = excelGen;
        this.outputDir = outputDir;
    }

    @Override
    public Mono<ReportJob> request(String reportType, String format, String parametersJson) {
        return TenantContext.getPrincipal()
                .flatMap(
                        principal -> {
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
                            job.setExpiresAt(Instant.now().plusSeconds(72 * 3600)); // 72h default
                            return repo.save(job).doOnSuccess(this::processAsync);
                        });
    }

    public void processAsync(ReportJob job) {
        log.info("Starting async report generation: jobId={} type={}", job.getId(), job.getReportType());

        Mono.fromCallable(() -> {
                    byte[] bytes;
                    if ("PDF".equals(job.getFormat())) {
                        bytes = pdfGen.generate(job);
                    } else {
                        bytes = excelGen.generate(job);
                    }
                    Path dir = Paths.get(outputDir, job.getSchoolId());
                    Files.createDirectories(dir);
                    Path file = dir.resolve(job.getId() + "." + job.getFormat().toLowerCase());
                    Files.write(file, bytes);
                    return file.toAbsolutePath().toString();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(path -> repo.updateStatus(job.getId(), "PROCESSING", 10, null)
                        .then(repo.updateStatus(job.getId(), "COMPLETED", 100, path)))
                .doOnSuccess(v -> log.info("Report generated successfully: jobId={}", job.getId()))
                .doOnError(e -> {
                    log.error("Report generation failed for job {}: {}", job.getId(), e.getMessage());
                    repo.updateStatus(job.getId(), "FAILED", 0, null)
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnError(err -> log.error("Failed to update status to FAILED for job {}: {}", job.getId(), err.getMessage()))
                            .subscribe();
                })
                .subscribe();
    }

    @Override
    public Mono<ReportJob> getStatus(String jobId) {
        return TenantContext.getSchoolId()
                .flatMap(
                        sid -> repo.findByIdAndSchoolId(jobId, sid)
                                .switchIfEmpty(
                                        Mono.error(
                                                new ResourceNotFoundException(
                                                        "ReportJob", jobId))));
    }

    @Override
    public Flux<ReportJob> myJobs() {
        return TenantContext.getPrincipal()
                .flatMapMany(p -> repo.findByRequestedByAndSchoolId(p.userId(), p.schoolId()));
    }

    @Override
    public Mono<byte[]> download(String jobId) {
        return getStatus(jobId)
                .flatMap(
                        job -> {
                            if (!"COMPLETED".equals(job.getStatus())) {
                                return Mono.error(
                                        new IllegalStateException("El reporte aún no está listo."));
                            }
                            if (job.getFilePath() == null || job.getFilePath().isEmpty()) {
                                return Mono.error(
                                        new IllegalStateException("Ruta de archivo no disponible."));
                            }
                            return Mono.fromCallable(
                                    () -> {
                                        Path file = Paths.get(job.getFilePath());
                                        if (!Files.exists(file)) {
                                            throw new IOException(
                                                    "Archivo de reporte no encontrado: "
                                                            + job.getFilePath());
                                        }
                                        return Files.readAllBytes(file);
                                    });
                        });
    }
}
