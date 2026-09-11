package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.adapter;

import java.time.Instant;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pe.edu.fineflow.profile.domain.model.TeacherSpecialty;
import pe.edu.fineflow.profile.domain.port.out.TeacherSpecialtyRepositoryPort;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.TeacherSpecialtyEntity;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.repository.TeacherSpecialtyR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TeacherSpecialtyRepositoryAdapter implements TeacherSpecialtyRepositoryPort {

    private final TeacherSpecialtyR2dbcRepository repository;

    @Override
    public Mono<TeacherSpecialty> save(TeacherSpecialty specialty) {
        return repository.save(toEntity(specialty)).map(this::toModel);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<TeacherSpecialty> findByTeacher(String schoolId, String teacherId) {
        return repository
                .findBySchoolIdAndTeacherId(schoolId, teacherId)
                .map(this::toModel);
    }

    private TeacherSpecialtyEntity toEntity(TeacherSpecialty m) {
        TeacherSpecialtyEntity e = new TeacherSpecialtyEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setTeacherId(m.getTeacherId());
        e.setSubject(m.getSubject());
        e.setSpecialtyLevel(m.getSpecialtyLevel() != null ? m.getSpecialtyLevel() : "SECONDARY");
        e.setIsPrimary(m.getIsPrimary() != null && m.getIsPrimary() == 1 ? 1 : 0);
        e.setCertified(m.getCertified() != null && m.getCertified() == 1 ? 1 : 0);
        e.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt() : Instant.now());
        return e;
    }

    private TeacherSpecialty toModel(TeacherSpecialtyEntity e) {
        TeacherSpecialty m = new TeacherSpecialty();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setTeacherId(e.getTeacherId());
        m.setSubject(e.getSubject());
        m.setSpecialtyLevel(e.getSpecialtyLevel());
        m.setIsPrimary(e.getIsPrimary());
        m.setCertified(e.getCertified());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}