package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.PatientDTO;
import com.medicalrecords.demo.entity.PatientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "gpDoctor", ignore = true)
    PatientEntity toEntity(PatientDTO dto);

    @Mapping(source = "gpDoctor.id", target = "gpDoctorId")
    PatientDTO toDto(PatientEntity entity);

    @Mapping(source = "gpDoctor.id", target = "gpDoctorId")
    List<PatientDTO> toDtoList(List<PatientEntity> entities);

    @Mapping(target = "gpDoctor", ignore = true)
    void updateEntityFromDto(@MappingTarget PatientEntity entity, PatientDTO dto);
}
