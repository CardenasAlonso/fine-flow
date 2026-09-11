package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.SystemConfigEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SystemConfigR2dbcRepository extends ReactiveCrudRepository<SystemConfigEntity, String> {
    Flux<SystemConfigEntity> findBySchoolId(String schoolId);

    Mono<SystemConfigEntity> findBySchoolIdAndConfigKey(String schoolId, String configKey);
}