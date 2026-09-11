package pe.edu.fineflow.support.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.application.port.in.ManageTranslationUseCase;
import pe.edu.fineflow.support.domain.model.Translation;
import pe.edu.fineflow.support.domain.port.out.TranslationRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TranslationUseCaseImpl implements ManageTranslationUseCase {

    private final TranslationRepositoryPort repo;

    public TranslationUseCaseImpl(TranslationRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Translation> upsert(Translation translation) {
        return repo.findByKeyAndLang(translation.getTransKey(), translation.getLangCode())
                .flatMap(
                        existing -> {
                            existing.setValue(translation.getValue());
                            existing.setContext(translation.getContext());
                            existing.setUpdatedAt(Instant.now());
                            return repo.save(existing);
                        })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            if (translation.getId() == null) {
                                translation.setId(UuidGenerator.generate());
                            }
                            translation.setUpdatedAt(Instant.now());
                            return repo.save(translation);
                        }));
    }

    @Override
    public Flux<Translation> findAll() {
        return repo.findByLangCode("es-PE");
    }

    @Override
    public Flux<Translation> findByLangCode(String langCode) {
        return repo.findByLangCode(langCode);
    }
}