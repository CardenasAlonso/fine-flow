package pe.edu.fineflow.support.domain.port.out;

import pe.edu.fineflow.support.domain.model.Translation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TranslationRepositoryPort {
    Mono<Translation> save(Translation translation);

    Flux<Translation> findByLangCode(String langCode);

    Mono<Translation> findByKeyAndLang(String transKey, String langCode);
}