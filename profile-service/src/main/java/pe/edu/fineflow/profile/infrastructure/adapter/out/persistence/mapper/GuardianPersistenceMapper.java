package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.edu.fineflow.profile.domain.model.Guardian;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.GuardianEntity;

@Component
public class GuardianPersistenceMapper {

    public Guardian toDomain(GuardianEntity entity) {
        if (entity == null) {
            return null;
        }
        Guardian guardian = new Guardian();
        guardian.setId(entity.getId());
        guardian.setSchoolId(entity.getSchoolId());
        guardian.setUserId(entity.getUserId());
        guardian.setStudentId(entity.getStudentId());
        guardian.setFirstName(entity.getFirstName());
        guardian.setLastName(entity.getLastName());
        guardian.setRelationship(entity.getRelationship());
        guardian.setPhone(entity.getPhone());
        guardian.setDocumentNumber(entity.getDocumentNumber());
        guardian.setEmail(entity.getEmail());
        guardian.setPrimaryContact(intToBoolean(entity.getIsPrimaryContact()));
        guardian.setCreatedAt(entity.getCreatedAt());
        return guardian;
    }

    public GuardianEntity toEntity(Guardian guardian) {
        if (guardian == null) {
            return null;
        }
        GuardianEntity entity = new GuardianEntity();
        entity.setId(guardian.getId());
        entity.setSchoolId(guardian.getSchoolId());
        entity.setUserId(guardian.getUserId());
        entity.setStudentId(guardian.getStudentId());
        entity.setFirstName(guardian.getFirstName());
        entity.setLastName(guardian.getLastName());
        entity.setRelationship(guardian.getRelationship());
        entity.setPhone(guardian.getPhone());
        entity.setDocumentNumber(guardian.getDocumentNumber());
        entity.setEmail(guardian.getEmail());
        entity.setIsPrimaryContact(booleanToInt(guardian.isPrimaryContact()));
        entity.setCreatedAt(guardian.getCreatedAt());
        return entity;
    }

    private boolean intToBoolean(Integer value) {
        return value != null && value == 1;
    }

    private Integer booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
}
