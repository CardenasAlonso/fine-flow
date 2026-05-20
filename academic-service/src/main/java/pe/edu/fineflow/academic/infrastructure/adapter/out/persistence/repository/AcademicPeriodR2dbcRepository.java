package pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.fineflow.academic.infrastructure.adapter.out.persistence.entity.AcademicPeriodEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AcademicPeriodR2dbcRepository
        extends R2dbcRepository<AcademicPeriodEntity, String> {
    Mono<AcademicPeriodEntity> findByIdAndSchoolId(String id, String schoolId);

    @Query("SELECT * FROM ACADEMIC_PERIODS WHERE school_id = :schoolId OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY")
    Flux<AcademicPeriodEntity> findAllBySchoolId(String schoolId, int offset, int limit);

    Flux<AcademicPeriodEntity> findAllBySchoolYearId(String schoolYearId);

    Mono<Void> deleteByIdAndSchoolId(String id, String schoolId);
}
