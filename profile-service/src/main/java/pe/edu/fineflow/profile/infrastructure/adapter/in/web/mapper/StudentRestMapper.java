package pe.edu.fineflow.profile.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pe.edu.fineflow.profile.domain.model.Student;
import pe.edu.fineflow.profile.infrastructure.adapter.in.web.dto.StudentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "bloodType", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "qrSecret", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toDomain(StudentDto.Create dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "documentNumber", ignore = true)
    @Mapping(target = "bloodType", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "qrSecret", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toDomainUpdate(StudentDto.Update dto);
}
