package pe.edu.fineflow.support.application.port.in;

import pe.edu.fineflow.support.domain.model.Translation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageTranslationUseCase {
    Mono<Translation> upsert(Translation translation);

    Flux<Translation> findAll();

    Flux<Translation> findByLangCode(String langCode);
}