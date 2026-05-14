package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.DiagnosisDTO;
import com.medicalrecords.demo.entity.DiagnosisEntity;
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
public class DiagnosisMapperImpl implements DiagnosisMapper {

    @Override
    public DiagnosisEntity toEntity(DiagnosisDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DiagnosisEntity diagnosisEntity = new DiagnosisEntity();

        diagnosisEntity.setId( dto.getId() );
        diagnosisEntity.setName( dto.getName() );
        diagnosisEntity.setDescription( dto.getDescription() );

        return diagnosisEntity;
    }

    @Override
    public DiagnosisDTO toDto(DiagnosisEntity entity) {
        if ( entity == null ) {
            return null;
        }

        DiagnosisDTO.DiagnosisDTOBuilder diagnosisDTO = DiagnosisDTO.builder();

        diagnosisDTO.id( entity.getId() );
        diagnosisDTO.name( entity.getName() );
        diagnosisDTO.description( entity.getDescription() );

        return diagnosisDTO.build();
    }

    @Override
    public List<DiagnosisDTO> toDtoList(List<DiagnosisEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DiagnosisDTO> list = new ArrayList<DiagnosisDTO>( entities.size() );
        for ( DiagnosisEntity diagnosisEntity : entities ) {
            list.add( toDto( diagnosisEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(DiagnosisEntity entity, DiagnosisDTO dto) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setName( dto.getName() );
        entity.setDescription( dto.getDescription() );
    }
}
