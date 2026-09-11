package pe.edu.fineflow.support.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.support.domain.model.Translation;
import pe.edu.fineflow.support.domain.port.out.TranslationRepositoryPort;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.entity.TranslationEntity;
import pe.edu.fineflow.support.infrastructure.adapter.out.persistence.repository.TranslationR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TranslationRepositoryAdapter implements TranslationRepositoryPort {

    private final TranslationR2dbcRepository repository;

    @Override
    public Mono<Translation> save(Translation translation) {
        return repository.save(toEntity(translation)).map(this::toModel);
    }

    @Override
    public Flux<Translation> findByLangCode(String langCode) {
        return repository.findByLangCode(langCode).map(this::toModel);
    }

    @Override
    public Mono<Translation> findByKeyAndLang(String transKey, String langCode) {
        return repository.findByTransKeyAndLangCode(transKey, langCode).map(this::toModel);
    }

    private TranslationEntity toEntity(Translation m) {
        TranslationEntity e = new TranslationEntity();
        e.setId(m.getId());
        e.setTransKey(m.getTransKey());
        e.setLangCode(m.getLangCode());
        e.setValue(m.getValue());
        e.setContext(m.getContext());
        e.setUpdatedAt(m.getUpdatedAt() != null ? m.getUpdatedAt() : Instant.now());
        return e;
    }

    private Translation toModel(TranslationEntity e) {
        Translation m = new Translation();
        m.setId(e.getId());
        m.setTransKey(e.getTransKey());
        m.setLangCode(e.getLangCode());
        m.setValue(e.getValue());
        m.setContext(e.getContext());
        m.setUpdatedAt(e.getUpdatedAt());
        return m;
    }
}