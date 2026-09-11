package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.identity.domain.port.out.SchoolDirectoryPort;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.repository.SchoolR2dbcRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SchoolRepositoryAdapter implements SchoolDirectoryPort {

    private final SchoolR2dbcRepository repository;

    @Override
    public Mono<Boolean> existsById(String schoolId) {
        return repository.existsById(schoolId);
    }
}