package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.SickLeaveDTO;
import com.medicalrecords.demo.entity.SickLeaveEntity;
import com.medicalrecords.demo.entity.VisitEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T10:35:09+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class SickLeaveMapperImpl implements SickLeaveMapper {

    @Override
    public SickLeaveEntity toEntity(SickLeaveDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SickLeaveEntity sickLeaveEntity = new SickLeaveEntity();

        sickLeaveEntity.setVisit( sickLeaveDTOToVisitEntity( dto ) );
        sickLeaveEntity.setId( dto.getId() );
        sickLeaveEntity.setStartDate( dto.getStartDate() );
        sickLeaveEntity.setNumberOfDays( dto.getNumberOfDays() );

        return sickLeaveEntity;
    }

    @Override
    public SickLeaveDTO toDto(SickLeaveEntity entity) {
        if ( entity == null ) {
            return null;
        }

        SickLeaveDTO.SickLeaveDTOBuilder sickLeaveDTO = SickLeaveDTO.builder();

        sickLeaveDTO.visitId( entityVisitId( entity ) );
        sickLeaveDTO.id( entity.getId() );
        sickLeaveDTO.startDate( entity.getStartDate() );
        sickLeaveDTO.numberOfDays( entity.getNumberOfDays() );

        return sickLeaveDTO.build();
    }

    @Override
    public List<SickLeaveDTO> toDtoList(List<SickLeaveEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SickLeaveDTO> list = new ArrayList<SickLeaveDTO>( entities.size() );
        for ( SickLeaveEntity sickLeaveEntity : entities ) {
            list.add( toDto( sickLeaveEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(SickLeaveEntity entity, SickLeaveDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( entity.getVisit() == null ) {
            entity.setVisit( new VisitEntity() );
        }
        sickLeaveDTOToVisitEntity1( dto, entity.getVisit() );
        entity.setId( dto.getId() );
        entity.setStartDate( dto.getStartDate() );
        entity.setNumberOfDays( dto.getNumberOfDays() );
    }

    protected VisitEntity sickLeaveDTOToVisitEntity(SickLeaveDTO sickLeaveDTO) {
        if ( sickLeaveDTO == null ) {
            return null;
        }

        VisitEntity visitEntity = new VisitEntity();

        visitEntity.setId( sickLeaveDTO.getVisitId() );

        return visitEntity;
    }

    private Long entityVisitId(SickLeaveEntity sickLeaveEntity) {
        if ( sickLeaveEntity == null ) {
            return null;
        }
        VisitEntity visit = sickLeaveEntity.getVisit();
        if ( visit == null ) {
            return null;
        }
        Long id = visit.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected void sickLeaveDTOToVisitEntity1(SickLeaveDTO sickLeaveDTO, VisitEntity mappingTarget) {
        if ( sickLeaveDTO == null ) {
            return;
        }

        mappingTarget.setId( sickLeaveDTO.getVisitId() );
    }
}
