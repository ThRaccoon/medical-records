package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.DiagnosisDTO;
import com.medicalrecords.demo.dto.VisitDTO;
import com.medicalrecords.demo.entity.*;
import com.medicalrecords.demo.exceptions.AccessDeniedException;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.DiagnosisMapper;
import com.medicalrecords.demo.mapper.VisitMapper;
import com.medicalrecords.demo.repository.VisitRepository;
import com.medicalrecords.demo.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitMapper visitMapper;
    private final VisitRepository visitRepository;
    private final DiagnosisMapper diagnosisMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public void upsert(VisitDTO dto) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == AppUserEntity.Role.DOCTOR) {
            DoctorEntity currentDoctor = securityUtils.getCurrentDoctor();
            if (dto.getDoctorId() != null && !currentDoctor.getId().equals(dto.getDoctorId())) {
                throw new AccessDeniedException("You can only create/edit your own visits");
            }
        }

        if (dto.getId() == null) {
            visitRepository.save(visitMapper.toEntity(dto));
            return;
        }

        VisitEntity entity = visitRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        visitMapper.updateEntityFromDto(entity, dto);
        visitRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public VisitDTO readById(Long id) {
        return visitMapper.toDto(visitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found")));
    }

    @Transactional(readOnly = true)
    public List<VisitDTO> readAll() {
        return visitMapper.toDtoList(visitRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() != AppUserEntity.Role.ADMIN) {
            throw new AccessDeniedException("Only admin can delete visits");
        }
        visitRepository.deleteById(id);
    }

    /// /////// Custom //////////

    @Transactional(readOnly = true)
    public List<VisitDTO> findByPatientId(Long patientId) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == AppUserEntity.Role.PATIENT) {
            PatientEntity currentPatient = securityUtils.getCurrentPatient();
            if (!currentPatient.getId().equals(patientId)) {
                throw new AccessDeniedException("Access denied");
            }
        }

        return visitMapper.toDtoList(visitRepository.findByPatientId(patientId));
    }

    @Transactional(readOnly = true)
    public List<VisitDTO> findByDoctorId(Long doctorId) {
        return visitMapper.toDtoList(visitRepository.findByDoctorId(doctorId));
    }

    @Transactional(readOnly = true)
    public List<VisitDTO> findByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to) {
        return visitMapper.toDtoList(visitRepository.findByDoctorAndPeriod(doctorId, from, to));
    }

    @Transactional(readOnly = true)
    public BigDecimal totalPaidByPatients() {
        return visitRepository.totalPaidByPatients();
    }

    @Transactional(readOnly = true)
    public List<Object[]> totalPaidByPatientsPerDoctor() {
        return visitRepository.totalPaidByPatientsPerDoctor();
    }

    @Transactional(readOnly = true)
    public List<Object[]> countVisitsPerDoctor() {
        return visitRepository.countVisitsPerDoctor();
    }

    @Transactional(readOnly = true)
    public DiagnosisDTO findMostCommonDiagnosis() {
        List<DiagnosisEntity> result = visitRepository.findMostCommonDiagnosis(PageRequest.of(0, 1));
        if (result.isEmpty()) throw new EntityNotFoundException("No diagnoses found");
        return diagnosisMapper.toDto(result.getFirst());
    }
}
