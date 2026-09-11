package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.StudentSectionHistoryEntity;
import reactor.core.publisher.Flux;

public interface StudentSectionHistoryR2dbcRepository
        extends ReactiveCrudRepository<StudentSectionHistoryEntity, String> {
    Flux<StudentSectionHistoryEntity> findBySchoolIdAndStudentIdOrderByChangedAtDesc(
            String schoolId, String studentId);

    Flux<StudentSectionHistoryEntity> findBySchoolIdOrderByChangedAtDesc(String schoolId);
}