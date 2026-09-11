package pe.edu.fineflow.report.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportJob {
    private String id;
    private String schoolId;
    private String requestedBy;
    private String reportType;
    private String format;
    private String parametersJson;
    private String status;
    private String filePath;
    private String errorDetail;
    private Long fileSizeKb;
    private int progressPct;
    private Instant requestedAt;
    private Instant startedAt;
    private Instant completedAt;
private Instant expiresAt;
  private int downloadCount;

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
