package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.TranslationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TranslationR2dbcRepository extends ReactiveCrudRepository<TranslationEntity, String> {
    Flux<TranslationEntity> findByLangCode(String langCode);

    Mono<TranslationEntity> findByTransKeyAndLangCode(String transKey, String langCode);
}