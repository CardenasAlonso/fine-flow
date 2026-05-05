package pe.edu.fineflow.academic.domain.port.out;

import pe.edu.fineflow.academic.domain.model.Section;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SectionRepositoryPort {
    Mono<Section> save(Section section);

    Mono<Section> findById(String id);

    Flux<Section> findAllBySchoolId(String schoolId);

    Flux<Section> findAllActiveBySchoolId(String schoolId);

    Flux<Section> findAllBySchoolId(String schoolId, int offset, int limit);

    Flux<Section> findAllActiveBySchoolId(String schoolId, int offset, int limit);

    Flux<Section> findAllBySchoolYearId(String schoolYearId);

    Mono<Void> deleteById(String id);
}
