package pe.edu.fineflow.academic.domain.port.out;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.academic.domain.model.Section;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SectionRepositoryPort {
    Mono<Section> save(Section section);

    Mono<Section> findById(String id);

    Flux<Section> findAllBySchoolId(String schoolId);

    Flux<Section> findAllActiveBySchoolId(String schoolId);

    Flux<Section> findAllBySchoolId(String schoolId, Pageable pageable);

    Flux<Section> findAllActiveBySchoolId(String schoolId, Pageable pageable);

    Flux<Section> findAllBySchoolYearId(String schoolYearId);

    Mono<Void> deleteById(String id);
}
