package pe.edu.fineflow.report.domain.port.out;

import reactor.core.publisher.Mono;

public interface ReportStoragePort {
    Mono<String> store(String schoolId, String jobId, String format, byte[] bytes);

    Mono<byte[]> load(String filePath);
}