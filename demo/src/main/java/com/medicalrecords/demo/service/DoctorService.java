package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.DoctorDTO;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.DoctorMapper;
import com.medicalrecords.demo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;

    @Transactional
    public void upsert(DoctorDTO dto) {
        if (dto.getId() == null) {
            doctorRepository.save(doctorMapper.toEntity(dto));
            return;
        }

        DoctorEntity entity = doctorRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));


        doctorMapper.updateEntityFromDto(entity, dto);
        doctorRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public DoctorDTO readById(Long id) {
        return doctorMapper.toDto(doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found")));
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> readAll() {
        return doctorMapper.toDtoList(doctorRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }
}
