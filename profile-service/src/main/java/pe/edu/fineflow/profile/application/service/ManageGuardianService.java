package pe.edu.fineflow.profile.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.port.BaseTenantRepositoryPort;
import pe.edu.fineflow.common.service.BaseTenantService;
import pe.edu.fineflow.profile.application.port.in.ManageGuardianUseCase;
import pe.edu.fineflow.profile.domain.model.Guardian;
import pe.edu.fineflow.profile.domain.port.out.GuardianRepositoryPort;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ManageGuardianService extends BaseTenantService<Guardian> implements ManageGuardianUseCase {
    private final GuardianRepositoryPort repository;

    @Override
    protected BaseTenantRepositoryPort<Guardian> getRepository() { return repository; }

    @Override
    protected String entityName() { return "Guardian"; }

    @Override
    protected void applyUpdate(Guardian existing, Guardian updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setRelationship(updated.getRelationship());
        existing.setPhone(updated.getPhone());
        existing.setDocumentNumber(updated.getDocumentNumber());
        existing.setEmail(updated.getEmail());
        existing.setPrimaryContact(updated.isPrimaryContact());
    }

    @Override
    public Flux<Guardian> findByStudent(String studentId) {
        return repository.findAllByStudentId(studentId);
    }
}
