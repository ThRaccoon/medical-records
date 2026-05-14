package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.PatientDTO;
import com.medicalrecords.demo.entity.AppUserEntity;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.entity.PatientEntity;
import com.medicalrecords.demo.exceptions.AccessDeniedException;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.PatientMapper;
import com.medicalrecords.demo.repository.DoctorRepository;
import com.medicalrecords.demo.repository.PatientRepository;
import com.medicalrecords.demo.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public void upsert(PatientDTO dto) {
        DoctorEntity gpDoctor = doctorRepository.findById(dto.getGpDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        if (dto.getId() == null) {
            PatientEntity entity = patientMapper.toEntity(dto);
            entity.setGpDoctor(gpDoctor);
            patientRepository.save(entity);
            return;
        }

        PatientEntity entity = patientRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        patientMapper.updateEntityFromDto(entity, dto);
        entity.setGpDoctor(gpDoctor);
        patientRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public PatientDTO readById(Long id) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == AppUserEntity.Role.PATIENT) {
            PatientEntity currentPatient = securityUtils.getCurrentPatient();
            if (!currentPatient.getId().equals(id)) {
                throw new AccessDeniedException("Access denied");
            }
        }
        return patientMapper.toDto(patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found")));
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> readAll() {
        AppUserEntity currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == AppUserEntity.Role.PATIENT) {
            throw new AccessDeniedException("Access denied");
        }
        return patientMapper.toDtoList(patientRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    /// /////// Custom //////////

    @Transactional(readOnly = true)
    public List<PatientDTO> findByDiagnosisId(Long diagnosisId) {
        return patientMapper.toDtoList(patientRepository.findByDiagnosisId(diagnosisId));
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> findByGpDoctorId(Long gpDoctorId) {
        return patientMapper.toDtoList(patientRepository.findByGpDoctorId(gpDoctorId));
    }

    @Transactional(readOnly = true)
    public List<Object[]> countPatientsPerGp() {
        return patientRepository.countPatientsPerGp();
    }
}
