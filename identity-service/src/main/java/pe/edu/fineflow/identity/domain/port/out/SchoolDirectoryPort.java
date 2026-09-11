package pe.edu.fineflow.identity.domain.port.out;

import reactor.core.publisher.Mono;

public interface SchoolDirectoryPort {
    Mono<Boolean> existsById(String schoolId);
}