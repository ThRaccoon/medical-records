package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.DiagnosisDTO;
import com.medicalrecords.demo.entity.DiagnosisEntity;
import com.medicalrecords.demo.entity.PatientEntity;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.DiagnosisMapper;
import com.medicalrecords.demo.repository.DiagnosisRepository;
import com.medicalrecords.demo.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisService {
    private final DiagnosisMapper diagnosisMapper;
    private final DiagnosisRepository diagnosisRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public void upsert(DiagnosisDTO dto) {
        if (dto.getId() == null) {
            diagnosisRepository.save(diagnosisMapper.toEntity(dto));
            return;
        }

        DiagnosisEntity entity = diagnosisRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        diagnosisMapper.updateEntityFromDto(entity, dto);
        diagnosisRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public DiagnosisDTO readById(Long id) {
        return diagnosisMapper.toDto(diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found")));
    }

    @Transactional(readOnly = true)
    public List<DiagnosisDTO> readAll() {
        if (securityUtils.isPatient()) {
            PatientEntity patient = securityUtils.getCurrentPatient();
            return diagnosisMapper.toDtoList(
                    diagnosisRepository.findByPatientId(patient.getId())
            );
        }
        return diagnosisMapper.toDtoList(diagnosisRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        diagnosisRepository.deleteById(id);
    }
}
