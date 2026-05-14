package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.VisitDTO;
import com.medicalrecords.demo.entity.VisitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(target = "doctor.id", source = "doctorId")
    @Mapping(target = "patient.id", source = "patientId")
    @Mapping(target = "diagnosis.id", source = "diagnosisId")
    VisitEntity toEntity(VisitDTO dto);

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "diagnosisId", source = "diagnosis.id")
    VisitDTO toDto(VisitEntity entity);

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "diagnosisId", source = "diagnosis.id")
    List<VisitDTO> toDtoList(List<VisitEntity> entities);

    @Mapping(target = "doctor.id", source = "doctorId")
    @Mapping(target = "patient.id", source = "patientId")
    @Mapping(target = "diagnosis.id", source = "diagnosisId")
    void updateEntityFromDto(@MappingTarget VisitEntity entity, VisitDTO dto);
}
