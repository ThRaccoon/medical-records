package com.medicalrecords.demo.repository;

import com.medicalrecords.demo.entity.SickLeaveEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeaveEntity, Long> {
    @Query("SELECT MONTH(s.startDate), YEAR(s.startDate), COUNT(s) FROM SickLeaveEntity s GROUP BY YEAR(s.startDate), MONTH(s.startDate) ORDER BY COUNT(s) DESC")
    List<Object[]> findMonthWithMostSickLeaves(Pageable pageable);

    @Query("SELECT s.visit.doctor.id, s.visit.doctor.name, COUNT(s) FROM SickLeaveEntity s GROUP BY s.visit.doctor.id, s.visit.doctor.name ORDER BY COUNT(s) DESC")
    List<Object[]> findDoctorsWithMostSickLeaves(Pageable pageable);
}
