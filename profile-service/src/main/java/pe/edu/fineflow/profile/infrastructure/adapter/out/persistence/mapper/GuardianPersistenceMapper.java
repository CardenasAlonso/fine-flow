package pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Named;
import pe.edu.fineflow.profile.domain.model.Guardian;
import pe.edu.fineflow.profile.infrastructure.adapter.out.persistence.entity.GuardianEntity;

public interface GuardianPersistenceMapper {

    @Named("intToBoolean")
    static boolean intToBoolean(Integer value) {
        return value != null && value == 1;
    }

    @Named("booleanToInt")
    static Integer booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    default Guardian toDomain(GuardianEntity entity) {
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

    default GuardianEntity toEntity(Guardian guardian) {
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
}
