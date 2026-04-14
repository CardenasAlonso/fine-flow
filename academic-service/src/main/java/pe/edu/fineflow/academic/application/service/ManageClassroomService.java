package pe.edu.fineflow.academic.application.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageClassroomUseCase;
import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.academic.domain.port.out.ClassroomRepositoryPort;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageClassroomService implements ManageClassroomUseCase {
    private final ClassroomRepositoryPort repository;

    @Override
    public Mono<Classroom> create(Classroom classroom) {
        return TenantContext.getSchoolId()
                .flatMap(
                        schoolId -> {
                            classroom.setId(UuidGenerator.generate());
                            classroom.setSchoolId(schoolId);
                            classroom.setIsActive(1);
                            classroom.setCreatedAt(Instant.now());
                            return repository.save(classroom);
                        });
    }

    @Override
    public Mono<Classroom> update(String id, Classroom updated) {
        return repository
                .findById(id)
                .flatMap(
                        existing -> {
                            existing.setName(updated.getName());
                            existing.setRoomType(updated.getRoomType());
                            existing.setCapacity(updated.getCapacity());
                            existing.setFloorNumber(updated.getFloorNumber());
                            existing.setBuilding(updated.getBuilding());
                            existing.setHasProjector(updated.getHasProjector());
                            existing.setHasComputers(updated.getHasComputers());
                            existing.setIsActive(updated.getIsActive());
                            existing.setNotes(updated.getNotes());
                            return repository.save(existing);
                        });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Classroom> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Classroom> findAll() {
        return TenantContext.getSchoolId().flatMapMany(repository::findAllBySchoolId);
    }

    @Override
    public Flux<Classroom> findAllActive() {
        return TenantContext.getSchoolId().flatMapMany(repository::findAllActiveBySchoolId);
    }
}
