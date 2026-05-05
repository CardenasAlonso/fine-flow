package pe.edu.fineflow.profile.domain.port.out;

import org.springframework.data.domain.Pageable;
import pe.edu.fineflow.profile.domain.model.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentRepositoryPort {
    Mono<Student> save(Student student);

    Mono<Student> findByIdAndSchoolId(String id, String schoolId);

    Flux<Student> findAllBySchoolId(String schoolId, Pageable pageable);

    Flux<Student> findAllBySectionIdAndSchoolId(String sectionId, String schoolId);

    Mono<Boolean> existsByDocumentNumberAndSchoolId(String documentNumber, String schoolId);

    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);

    Flux<Student> searchBySchoolId(String schoolId, String query);
}
