package pe.edu.fineflow.support.infrastructure.adapter.in.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.fineflow.support.application.port.in.ManageTranslationUseCase;
import pe.edu.fineflow.support.domain.model.Translation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support/translations")
@Tag(name = "Traducciones", description = "Internacionalización multi-idioma")
public class TranslationController {

    private final ManageTranslationUseCase useCase;

    public TranslationController(ManageTranslationUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<Translation> findAll() {
        return useCase.findAll();
    }

    @GetMapping("/{langCode}")
    @PreAuthorize("isAuthenticated()")
    public Flux<Translation> findByLang(@PathVariable String langCode) {
        return useCase.findByLangCode(langCode);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<Translation> upsert(@RequestBody Translation translation) {
        return useCase.upsert(translation);
    }
}