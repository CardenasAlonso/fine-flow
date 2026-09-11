package pe.edu.fineflow.report.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportJobTest {

    private ReportJob job(String format) {
        ReportJob j = new ReportJob();
        j.setFormat(format);
        return j;
    }

    @Test
    void pdfFormatIsDetected() {
        assertThat(job("PDF").isPdf()).isTrue();
        assertThat(job("pdf").isPdf()).isTrue();
    }

    @Test
    void excelFormatIsNotPdf() {
        assertThat(job("EXCEL").isPdf()).isFalse();
        assertThat(job("XLSX").isPdf()).isFalse();
    }

    @Test
    void contentTypeForPdf() {
        assertThat(job("PDF").contentType()).isEqualTo("application/pdf");
    }

    @Test
    void contentTypeForExcel() {
        assertThat(job("EXCEL").contentType())
                .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Test
    void fileExtensionForPdf() {
        assertThat(job("PDF").fileExtension()).isEqualTo("pdf");
    }

    @Test
    void fileExtensionForExcel() {
        assertThat(job("EXCEL").fileExtension()).isEqualTo("excel");
    }

    @Test
    void fileExtensionDefaultsToBin() {
        assertThat(job(null).fileExtension()).isEqualTo("bin");
    }
}