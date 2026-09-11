package pe.edu.fineflow.report.domain.port.out;

import pe.edu.fineflow.report.domain.model.ReportJob;

public interface ReportGeneratorPort {
    byte[] generate(ReportJob job);
}