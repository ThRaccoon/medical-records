package com.medicalrecords.demo.mapper;

import com.medicalrecords.demo.dto.VisitDTO;
import com.medicalrecords.demo.entity.DiagnosisEntity;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.entity.PatientEntity;
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
public class VisitMapperImpl implements VisitMapper {

    @Override
    public VisitEntity toEntity(VisitDTO dto) {
        if ( dto == null ) {
            return null;
        }

        VisitEntity visitEntity = new VisitEntity();

        visitEntity.setDoctor( visitDTOToDoctorEntity( dto ) );
        visitEntity.setPatient( visitDTOToPatientEntity( dto ) );
        visitEntity.setDiagnosis( visitDTOToDiagnosisEntity( dto ) );
        visitEntity.setId( dto.getId() );
        visitEntity.setDate( dto.getDate() );
        visitEntity.setTreatment( dto.getTreatment() );
        visitEntity.setPrice( dto.getPrice() );
        visitEntity.setPaidByNzok( dto.isPaidByNzok() );

        return visitEntity;
    }

    @Override
    public VisitDTO toDto(VisitEntity entity) {
        if ( entity == null ) {
            return null;
        }

        VisitDTO.VisitDTOBuilder visitDTO = VisitDTO.builder();

        visitDTO.doctorId( entityDoctorId( entity ) );
        visitDTO.patientId( entityPatientId( entity ) );
        visitDTO.diagnosisId( entityDiagnosisId( entity ) );
        visitDTO.id( entity.getId() );
        visitDTO.date( entity.getDate() );
        visitDTO.treatment( entity.getTreatment() );
        visitDTO.price( entity.getPrice() );
        visitDTO.paidByNzok( entity.isPaidByNzok() );

        return visitDTO.build();
    }

    @Override
    public List<VisitDTO> toDtoList(List<VisitEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<VisitDTO> list = new ArrayList<VisitDTO>( entities.size() );
        for ( VisitEntity visitEntity : entities ) {
            list.add( toDto( visitEntity ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(VisitEntity entity, VisitDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( entity.getDoctor() == null ) {
            entity.setDoctor( new DoctorEntity() );
        }
        visitDTOToDoctorEntity1( dto, entity.getDoctor() );
        if ( entity.getPatient() == null ) {
            entity.setPatient( new PatientEntity() );
        }
        visitDTOToPatientEntity1( dto, entity.getPatient() );
        if ( entity.getDiagnosis() == null ) {
            entity.setDiagnosis( new DiagnosisEntity() );
        }
        visitDTOToDiagnosisEntity1( dto, entity.getDiagnosis() );
        entity.setId( dto.getId() );
        entity.setDate( dto.getDate() );
        entity.setTreatment( dto.getTreatment() );
        entity.setPrice( dto.getPrice() );
        entity.setPaidByNzok( dto.isPaidByNzok() );
    }

    protected DoctorEntity visitDTOToDoctorEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        DoctorEntity doctorEntity = new DoctorEntity();

        doctorEntity.setId( visitDTO.getDoctorId() );

        return doctorEntity;
    }

    protected PatientEntity visitDTOToPatientEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        PatientEntity patientEntity = new PatientEntity();

        patientEntity.setId( visitDTO.getPatientId() );

        return patientEntity;
    }

    protected DiagnosisEntity visitDTOToDiagnosisEntity(VisitDTO visitDTO) {
        if ( visitDTO == null ) {
            return null;
        }

        DiagnosisEntity diagnosisEntity = new DiagnosisEntity();

        diagnosisEntity.setId( visitDTO.getDiagnosisId() );

        return diagnosisEntity;
    }

    private Long entityDoctorId(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }
        DoctorEntity doctor = visitEntity.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Long id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityPatientId(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }
        PatientEntity patient = visitEntity.getPatient();
        if ( patient == null ) {
            return null;
        }
        Long id = patient.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityDiagnosisId(VisitEntity visitEntity) {
        if ( visitEntity == null ) {
            return null;
        }
        DiagnosisEntity diagnosis = visitEntity.getDiagnosis();
        if ( diagnosis == null ) {
            return null;
        }
        Long id = diagnosis.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected void visitDTOToDoctorEntity1(VisitDTO visitDTO, DoctorEntity mappingTarget) {
        if ( visitDTO == null ) {
            return;
        }

        mappingTarget.setId( visitDTO.getDoctorId() );
    }

    protected void visitDTOToPatientEntity1(VisitDTO visitDTO, PatientEntity mappingTarget) {
        if ( visitDTO == null ) {
            return;
        }

        mappingTarget.setId( visitDTO.getPatientId() );
    }

    protected void visitDTOToDiagnosisEntity1(VisitDTO visitDTO, DiagnosisEntity mappingTarget) {
        if ( visitDTO == null ) {
            return;
        }

        mappingTarget.setId( visitDTO.getDiagnosisId() );
    }
}
