package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.DoctorDTO;
import com.medicalrecords.demo.entity.DoctorEntity;
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
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public DoctorEntity toEntity(DoctorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DoctorEntity doctorEntity = new DoctorEntity();

        doctorEntity.setId( dto.getId() );
        doctorEntity.setUin( dto.getUin() );
        doctorEntity.setName( dto.getName() );
        doctorEntity.setSpeciality( dto.getSpeciality() );
        doctorEntity.setGp( dto.isGp() );

        return doctorEntity;
    }

    @Override
    public DoctorDTO toDto(DoctorEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DoctorDTO.DoctorDTOBuilder doctorDTO = DoctorDTO.builder();

        doctorDTO.id( entity.getId() );
        doctorDTO.uin( entity.getUin() );
        doctorDTO.name( entity.getName() );
        doctorDTO.speciality( entity.getSpeciality() );
        doctorDTO.gp( entity.isGp() );

        return doctorDTO.build();
    }

    @Override
    public List<DoctorDTO> toDtoList(List<DoctorEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DoctorDTO> list = new ArrayList<DoctorDTO>( entities.size() );
        for ( DoctorEntity doctorEntity : entities ) {
            list.add( toDto( doctorEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(DoctorEntity entity, DoctorDTO dto) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setUin( dto.getUin() );
        entity.setName( dto.getName() );
        entity.setSpeciality( dto.getSpeciality() );
        entity.setGp( dto.isGp() );
    }
}
