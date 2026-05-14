package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.DiagnosisDTO;
import com.medicalrecords.demo.entity.DiagnosisEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {
    DiagnosisEntity toEntity(DiagnosisDTO dto);

    DiagnosisDTO toDto(DiagnosisEntity entity);

    List<DiagnosisDTO> toDtoList(List<DiagnosisEntity> entities);

    void updateEntityFromDto(@MappingTarget DiagnosisEntity entity, DiagnosisDTO dto);
}
