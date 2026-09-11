package pe.edu.fineflow.report.infrastructure.adapter.out.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.report.domain.port.out.ReportStoragePort;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class ReportStorageAdapter implements ReportStoragePort {

    private final String outputDir;

    public ReportStorageAdapter(
            @Value("${fineflow.reports.output-dir:/reports}") String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public Mono<String> store(String schoolId, String jobId, String format, byte[] bytes) {
        String ext = format == null ? "bin" : format.toLowerCase();
        return Mono.fromCallable(
                        () -> {
                            Path dir = Paths.get(outputDir, schoolId);
                            Files.createDirectories(dir);
                            Path file = dir.resolve(jobId + "." + ext);
                            Files.write(file, bytes);
                            return file.toAbsolutePath().toString();
                        })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<byte[]> load(String filePath) {
        return Mono.fromCallable(
                        () -> {
                            Path file = Paths.get(filePath);
                            if (!Files.exists(file)) {
                                throw new IOException("Archivo de reporte no encontrado: " + filePath);
                            }
                            return Files.readAllBytes(file);
                        })
                .subscribeOn(Schedulers.boundedElastic());
    }
}