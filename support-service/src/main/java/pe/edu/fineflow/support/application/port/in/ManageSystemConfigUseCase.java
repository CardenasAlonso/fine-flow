package pe.edu.fineflow.support.application.port.in;

import pe.edu.fineflow.support.domain.model.SystemConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageSystemConfigUseCase {
    Mono<SystemConfig> upsert(String key, String value, String valueType, String description);

    Mono<SystemConfig> findByKey(String key);

    Flux<SystemConfig> findAll();
}