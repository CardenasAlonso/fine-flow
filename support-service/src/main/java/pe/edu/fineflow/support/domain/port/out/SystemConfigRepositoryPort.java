package pe.edu.fineflow.support.domain.port.out;

import pe.edu.fineflow.support.domain.model.SystemConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SystemConfigRepositoryPort {
    Mono<SystemConfig> save(SystemConfig config);

    Mono<SystemConfig> findByKey(String schoolId, String configKey);

    Flux<SystemConfig> findBySchoolId(String schoolId);
}