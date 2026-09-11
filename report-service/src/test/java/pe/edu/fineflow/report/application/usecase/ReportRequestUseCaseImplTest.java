package pe.edu.fineflow.report.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.security.UserPrincipal;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.report.application.port.in.RequestReportUseCase.DownloadResult;
import pe.edu.fineflow.report.domain.model.ReportJob;
import pe.edu.fineflow.report.domain.port.out.ReportGeneratorPort;
import pe.edu.fineflow.report.domain.port.out.ReportJobRepositoryPort;
import pe.edu.fineflow.report.domain.port.out.ReportStoragePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ReportRequestUseCaseImplTest {

    private ReportJobRepositoryPort repo;
    private ReportGeneratorPort generator;
    private ReportStoragePort storage;
    private ReportRequestUseCaseImpl service;

    @BeforeEach
    void setUp() {
        repo = org.mockito.Mockito.mock(ReportJobRepositoryPort.class);
        generator = org.mockito.Mockito.mock(ReportGeneratorPort.class);
        storage = org.mockito.Mockito.mock(ReportStoragePort.class);
        service = new ReportRequestUseCaseImpl(repo, generator, storage);
    }

    private UserPrincipal principal() {
        return new UserPrincipal("user-1", "school-1", "ana@demo.edu.pe", "ADMIN", null);
    }

    private ReportJob completedJob() {
        ReportJob job = new ReportJob();
        job.setId("job-1");
        job.setSchoolId("school-1");
        job.setRequestedBy("user-1");
        job.setFormat("PDF");
        job.setStatus(ReportJob.Status.COMPLETED);
        job.setFilePath("/reports/school-1/job-1.pdf");
        return job;
    }

    @Test
    void requestCreatesPendingJobForPrincipal() {
        when(repo.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(generator.generate(any())).thenReturn(new byte[]{1, 2});
        when(storage.store(any(), any(), any(), any())).thenReturn(Mono.just("/reports/school-1/x.pdf"));
        when(repo.updateStatus(anyString(), any(), anyInt(), nullable(String.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.request("REPORT_CARDS", "PDF", "{}")
                        .contextWrite(ctx -> ctx.put(TenantContext.PRINCIPAL_KEY, principal())))
                .assertNext(job -> {
                    org.assertj.core.api.Assertions.assertThat(job.getStatus()).isEqualTo(ReportJob.Status.PENDING);
                    org.assertj.core.api.Assertions.assertThat(job.getRequestedBy()).isEqualTo("user-1");
                    org.assertj.core.api.Assertions.assertThat(job.getSchoolId()).isEqualTo("school-1");
                })
                .verifyComplete();

        verify(repo).save(any());
    }

    @Test
    void getStatusReturnsOnlyOwnedJob() {
        when(repo.findByIdAndSchoolId("job-1", "school-1")).thenReturn(Mono.just(completedJob()));

        StepVerifier.create(service.getStatus("job-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .assertNext(job -> org.assertj.core.api.Assertions.assertThat(job.getId()).isEqualTo("job-1"))
                .verifyComplete();
    }

    @Test
    void getStatusRejectsForeignJob() {
        when(repo.findByIdAndSchoolId("job-1", "school-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.getStatus("job-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void downloadReturnsPdfContentType() {
        when(repo.findByIdAndSchoolId("job-1", "school-1")).thenReturn(Mono.just(completedJob()));
        when(storage.load("/reports/school-1/job-1.pdf")).thenReturn(Mono.just(new byte[]{1, 2, 3}));

        StepVerifier.create(service.download("job-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .assertNext(result -> {
                    org.assertj.core.api.Assertions.assertThat(result.contentType()).isEqualTo("application/pdf");
                    org.assertj.core.api.Assertions.assertThat(result.fileName()).isEqualTo("report-job-1.pdf");
                    org.assertj.core.api.Assertions.assertThat(result.content()).containsExactly(1, 2, 3);
                })
                .verifyComplete();
    }

    @Test
    void downloadFailsWhenNotCompleted() {
        ReportJob pending = completedJob();
        pending.setStatus(ReportJob.Status.PENDING);
        when(repo.findByIdAndSchoolId("job-1", "school-1")).thenReturn(Mono.just(pending));

        StepVerifier.create(service.download("job-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void downloadFailsWhenFilePathMissing() {
        ReportJob job = completedJob();
        job.setFilePath(null);
        when(repo.findByIdAndSchoolId("job-1", "school-1")).thenReturn(Mono.just(job));

        StepVerifier.create(service.download("job-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void myJobsListsOnlyPrincipalJobs() {
        when(repo.findByRequestedByAndSchoolId("user-1", "school-1"))
                .thenReturn(Flux.just(completedJob()));

        StepVerifier.create(service.myJobs()
                        .contextWrite(ctx -> ctx.put(TenantContext.PRINCIPAL_KEY, principal())))
                .expectNextCount(1)
                .verifyComplete();
    }
}