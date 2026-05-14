package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.SickLeaveDTO;
import com.medicalrecords.demo.entity.SickLeaveEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SickLeaveMapper {
    @Mapping(target = "visit.id", source = "visitId")
    SickLeaveEntity toEntity(SickLeaveDTO dto);

    @Mapping(target = "visitId", source = "visit.id")
    SickLeaveDTO toDto(SickLeaveEntity entity);

    @Mapping(target = "visitId", source = "visit.id")
    List<SickLeaveDTO> toDtoList(List<SickLeaveEntity> entities);

    @Mapping(target = "visit.id", source = "visitId")
    void updateEntityFromDto(@MappingTarget SickLeaveEntity entity, SickLeaveDTO dto);
}
