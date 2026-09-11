package pe.edu.fineflow.report.domain.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportJob {

    public enum Status { PENDING, PROCESSING, COMPLETED, FAILED }

    private String id;
    private String schoolId;
    private String requestedBy;
    private String reportType;
    private String format;
    private String parametersJson;
    private Status status;
    private String filePath;
    private String errorDetail;
    private Long fileSizeKb;
    private int progressPct;
    private Instant requestedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant expiresAt;
    private int downloadCount;

    public static ReportJob create(String reportType, String format, String parametersJson,
                                    String schoolId, String requestedBy) {
        ReportJob job = new ReportJob();
        job.setReportType(reportType);
        job.setFormat(format);
        job.setParametersJson(parametersJson);
        job.setSchoolId(schoolId);
        job.setRequestedBy(requestedBy);
        job.setStatus(Status.PENDING);
        job.setProgressPct(0);
        job.setRequestedAt(Instant.now());
        job.setExpiresAt(Instant.now().plusSeconds(72 * 3600));
        return job;
    }

    public void startProcessing() {
        this.status = Status.PROCESSING;
        this.startedAt = Instant.now();
    }

    public void complete(String filePath) {
        this.status = Status.COMPLETED;
        this.filePath = filePath;
        this.progressPct = 100;
        this.completedAt = Instant.now();
    }

    public void fail(String errorDetail) {
        this.status = Status.FAILED;
        this.errorDetail = errorDetail;
        this.progressPct = 0;
    }

    public boolean isPdf() {
        return "PDF".equalsIgnoreCase(format);
    }

    public String contentType() {
        return isPdf()
            ? "application/pdf"
            : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    public String fileExtension() {
        return isPdf() ? "pdf" : format == null ? "bin" : format.toLowerCase();
    }
}