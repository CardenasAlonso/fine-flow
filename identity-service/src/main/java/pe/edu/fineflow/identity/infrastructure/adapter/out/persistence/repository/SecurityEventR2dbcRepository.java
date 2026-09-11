package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.SecurityEventEntity;

public interface SecurityEventR2dbcRepository
        extends ReactiveCrudRepository<SecurityEventEntity, String> {}