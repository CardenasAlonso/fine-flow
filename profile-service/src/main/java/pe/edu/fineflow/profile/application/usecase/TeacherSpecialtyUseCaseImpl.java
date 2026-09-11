package pe.edu.fineflow.profile.application.usecase;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.profile.application.port.in.ManageTeacherSpecialtyUseCase;
import pe.edu.fineflow.profile.domain.model.TeacherSpecialty;
import pe.edu.fineflow.profile.domain.port.out.TeacherSpecialtyRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TeacherSpecialtyUseCaseImpl implements ManageTeacherSpecialtyUseCase {

    private final TeacherSpecialtyRepositoryPort repo;

    public TeacherSpecialtyUseCaseImpl(TeacherSpecialtyRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<TeacherSpecialty> add(TeacherSpecialty specialty) {
        return TenantContext.getSchoolId()
                .flatMap(
                        sid -> {
                            if (specialty.getId() == null) {
                                specialty.setId(UuidGenerator.generate());
                            }
                            specialty.setSchoolId(sid);
                            specialty.setCreatedAt(Instant.now());
                            return repo.save(specialty);
                        });
    }

    @Override
    public Mono<Void> remove(String id) {
        return repo.delete(id)
                .onErrorResume(e -> Mono.error(new ResourceNotFoundException("TeacherSpecialty", id)));
    }

    @Override
    public Flux<TeacherSpecialty> findByTeacher(String teacherId) {
        return TenantContext.getSchoolId()
                .flatMapMany(sid -> repo.findByTeacher(sid, teacherId));
    }
}