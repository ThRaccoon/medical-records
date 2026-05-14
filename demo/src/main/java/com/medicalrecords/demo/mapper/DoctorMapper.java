package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.DoctorDTO;
import com.medicalrecords.demo.entity.DoctorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorEntity toEntity(DoctorDTO dto);

    DoctorDTO toDto(DoctorEntity entity);

    List<DoctorDTO> toDtoList(List<DoctorEntity> entities);

    void updateEntityFromDto(@MappingTarget DoctorEntity entity, DoctorDTO dto);
}
