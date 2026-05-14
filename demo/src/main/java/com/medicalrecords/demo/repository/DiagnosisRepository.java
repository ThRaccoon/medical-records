package com.medicalrecords.demo.repository;

import com.medicalrecords.demo.entity.DiagnosisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long> {
    @Query("SELECT DISTINCT v.diagnosis FROM VisitEntity v WHERE v.patient.id = :patientId")
    List<DiagnosisEntity> findByPatientId(@Param("patientId") Long patientId);
}
