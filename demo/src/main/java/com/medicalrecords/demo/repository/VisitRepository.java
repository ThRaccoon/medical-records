package com.medicalrecords.demo.repository;

import com.medicalrecords.demo.entity.DiagnosisEntity;
import com.medicalrecords.demo.entity.VisitEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitEntity, Long> {
    List<VisitEntity> findByPatientId(Long patientId);

    List<VisitEntity> findByDoctorId(Long doctorId);

    @Query("SELECT v FROM VisitEntity v WHERE " +
            "(:doctorId IS NULL OR v.doctor.id = :doctorId) AND " +
            "(:from IS NULL OR v.date >= :from) AND " +
            "(:to IS NULL OR v.date <= :to)")
    List<VisitEntity> findByDoctorAndPeriod(
            @Param("doctorId") Long doctorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("SELECT SUM(v.price) FROM VisitEntity v WHERE v.paidByNzok = false")
    BigDecimal totalPaidByPatients();

    @Query("SELECT v.doctor.id, v.doctor.name, SUM(v.price) FROM VisitEntity v WHERE v.paidByNzok = false GROUP BY v.doctor.id, v.doctor.name")
    List<Object[]> totalPaidByPatientsPerDoctor();

    @Query("SELECT v.doctor.id, v.doctor.name, COUNT(v) FROM VisitEntity v GROUP BY v.doctor.id, v.doctor.name")
    List<Object[]> countVisitsPerDoctor();

    @Query("SELECT v.diagnosis FROM VisitEntity v GROUP BY v.diagnosis ORDER BY COUNT(v) DESC")
    List<DiagnosisEntity> findMostCommonDiagnosis(Pageable pageable);
}
