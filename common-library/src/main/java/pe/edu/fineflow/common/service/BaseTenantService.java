package pe.edu.fineflow.common.service;

import pe.edu.fineflow.common.exception.BusinessException;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.model.BaseDomainEntity;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;

public abstract class BaseTenantService<T extends BaseDomainEntity> {

    protected abstract BaseTenantRepositoryPort<T> getRepository();
    protected abstract void applyUpdate(T existing, T updated);
    protected abstract String entityName();

    public Mono<T> create(T entity) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId -> {
                    entity.setId(UuidGenerator.generate());
                    entity.setSchoolId(schoolId);
                    entity.setCreatedAt(Instant.now());
                    return getRepository().save(entity);
                });
    }

    public Mono<T> update(String id, T updated) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId ->
                        getRepository().findByIdAndSchoolId(id, schoolId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(entityName(), id)))
                                .flatMap(existing -> {
                                    applyUpdate(existing, updated);
                                    return getRepository().save(existing);
                                }));
    }

    public Mono<Void> delete(String id) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId -> getRepository().deleteByIdAndSchoolId(id, schoolId));
    }

    public Mono<T> findById(String id) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId ->
                        getRepository().findByIdAndSchoolId(id, schoolId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(entityName(), id))));
    }

    public Flux<T> findAll(int offset, int limit) {
        return TenantContext.getSchoolId()
                .flatMapMany(schoolId -> getRepository().findAllBySchoolId(schoolId, offset, limit));
    }
}
