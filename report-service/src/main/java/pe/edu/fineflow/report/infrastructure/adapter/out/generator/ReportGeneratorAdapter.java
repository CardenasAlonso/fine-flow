package pe.edu.fineflow.report.infrastructure.adapter.out.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.report.domain.model.ReportJob;
import pe.edu.fineflow.report.domain.port.out.ReportGeneratorPort;
import pe.edu.fineflow.report.infrastructure.generator.ExcelReportGenerator;
import pe.edu.fineflow.report.infrastructure.generator.PdfReportGenerator;

@Component
@RequiredArgsConstructor
public class ReportGeneratorAdapter implements ReportGeneratorPort {

    private static final String PDF = "PDF";

    private final PdfReportGenerator pdfGen;
    private final ExcelReportGenerator excelGen;

    @Override
    public byte[] generate(ReportJob job) {
        try {
            return PDF.equalsIgnoreCase(job.getFormat())
                    ? pdfGen.generate(job)
                    : excelGen.generate(job);
        } catch (Exception e) {
            throw new RuntimeException("Error generando el reporte", e);
        }
    }
}