package pe.edu.fineflow.academic.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.academic.application.port.in.ManageClassroomUseCase;
import pe.edu.fineflow.academic.domain.model.Classroom;
import pe.edu.fineflow.academic.domain.port.out.ClassroomRepositoryPort;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.common.tenant.TenantContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ManageClassroomService extends BaseTenantService<Classroom> implements ManageClassroomUseCase {
    private final ClassroomRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<Classroom> getRepository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Aula";
    }

    @Override
    protected void applyUpdate(Classroom existing, Classroom updated) {
        existing.setName(updated.getName());
        existing.setRoomType(updated.getRoomType());
        existing.setCapacity(updated.getCapacity());
        existing.setFloorNumber(updated.getFloorNumber());
        existing.setBuilding(updated.getBuilding());
        existing.setHasProjector(updated.getHasProjector());
        existing.setHasComputers(updated.getHasComputers());
        existing.setIsActive(updated.getIsActive());
        existing.setNotes(updated.getNotes());
    }

    @Override
    public Mono<Classroom> create(Classroom classroom) {
        classroom.setIsActive(1);
        return super.create(classroom);
    }

    @Override
    public Flux<Classroom> findAllActive() {
        return TenantContext.getSchoolId().flatMapMany(repository::findAllActiveBySchoolId);
    }
}
