package pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import pe.edu.fineflow.identity.domain.model.User;
import pe.edu.fineflow.identity.infrastructure.adapter.out.persistence.entity.UserEntity;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPersistenceMapper {
    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}
