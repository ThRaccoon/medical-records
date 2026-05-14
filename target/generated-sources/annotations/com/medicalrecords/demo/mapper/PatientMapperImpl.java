package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.PatientDTO;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.entity.PatientEntity;
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
public class PatientMapperImpl implements PatientMapper {

    @Override
    public PatientEntity toEntity(PatientDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PatientEntity patientEntity = new PatientEntity();

        patientEntity.setId( dto.getId() );
        patientEntity.setName( dto.getName() );
        patientEntity.setEgn( dto.getEgn() );
        patientEntity.setInsured( dto.isInsured() );

        return patientEntity;
    }

    @Override
    public PatientDTO toDto(PatientEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PatientDTO.PatientDTOBuilder patientDTO = PatientDTO.builder();

        patientDTO.gpDoctorId( entityGpDoctorId( entity ) );
        patientDTO.id( entity.getId() );
        patientDTO.name( entity.getName() );
        patientDTO.egn( entity.getEgn() );
        patientDTO.insured( entity.isInsured() );

        return patientDTO.build();
    }

    @Override
    public List<PatientDTO> toDtoList(List<PatientEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PatientDTO> list = new ArrayList<PatientDTO>( entities.size() );
        for ( PatientEntity patientEntity : entities ) {
            list.add( toDto( patientEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(PatientEntity entity, PatientDTO dto) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setName( dto.getName() );
        entity.setEgn( dto.getEgn() );
        entity.setInsured( dto.isInsured() );
    }

    private Long entityGpDoctorId(PatientEntity patientEntity) {
        if ( patientEntity == null ) {
            return null;
        }
        DoctorEntity gpDoctor = patientEntity.getGpDoctor();
        if ( gpDoctor == null ) {
            return null;
        }
        Long id = gpDoctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
