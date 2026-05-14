package com.medicalrecords.demo.repository;

import com.medicalrecords.demo.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByAppUserId(Long appUserId);

    @Query("SELECT DISTINCT v.patient FROM VisitEntity v WHERE v.diagnosis.id = :diagnosisId")
    List<PatientEntity> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    List<PatientEntity> findByGpDoctorId(Long gpDoctorId);

    @Query("SELECT v.patient.gpDoctor.name, COUNT(DISTINCT v.patient.id) FROM VisitEntity v GROUP BY v.patient.gpDoctor.id, v.patient.gpDoctor.name")
    List<Object[]> countPatientsPerGp();
}
