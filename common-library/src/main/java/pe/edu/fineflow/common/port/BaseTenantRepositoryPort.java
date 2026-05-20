package pe.edu.fineflow.common.port;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseTenantRepositoryPort<T> {
    Mono<T> save(T entity);
    Mono<T> findByIdAndSchoolId(String id, String schoolId);
    Flux<T> findAllBySchoolId(String schoolId, int offset, int limit);
    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);
}
