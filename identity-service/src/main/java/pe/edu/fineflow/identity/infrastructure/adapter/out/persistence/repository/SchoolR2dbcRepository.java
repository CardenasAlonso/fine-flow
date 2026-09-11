package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.SchoolEntity;

public interface SchoolR2dbcRepository extends ReactiveCrudRepository<SchoolEntity, String> {}