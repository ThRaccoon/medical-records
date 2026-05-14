package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.SickLeaveDTO;
import com.medicalrecords.demo.entity.AppUserEntity;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.entity.SickLeaveEntity;
import com.medicalrecords.demo.entity.VisitEntity;
import com.medicalrecords.demo.exceptions.AccessDeniedException;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.SickLeaveMapper;
import com.medicalrecords.demo.repository.SickLeaveRepository;
import com.medicalrecords.demo.repository.VisitRepository;
import com.medicalrecords.demo.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SickLeaveService {
    private final SickLeaveMapper sickLeaveMapper;
    private final SickLeaveRepository sickLeaveRepository;
    private final VisitRepository visitRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public void upsert(SickLeaveDTO dto) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == AppUserEntity.Role.DOCTOR) {
            DoctorEntity currentDoctor = securityUtils.getCurrentDoctor();
            VisitEntity visit = visitRepository.findById(dto.getVisitId())
                    .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
            if (!currentDoctor.getId().equals(visit.getDoctor().getId())) {
                throw new AccessDeniedException("You can only create sick leaves for your own visits");
            }
        }

        if (dto.getId() == null) {
            sickLeaveRepository.save(sickLeaveMapper.toEntity(dto));
            return;
        }

        SickLeaveEntity entity = sickLeaveRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sick leave not found"));

        sickLeaveMapper.updateEntityFromDto(entity, dto);
        sickLeaveRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public SickLeaveDTO readById(Long id) {
        return sickLeaveMapper.toDto(sickLeaveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sick leave not found")));
    }

    @Transactional(readOnly = true)
    public List<SickLeaveDTO> readAll() {
        return sickLeaveMapper.toDtoList(sickLeaveRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() != AppUserEntity.Role.ADMIN) {
            throw new AccessDeniedException("Only admin can delete sick leaves");
        }
        sickLeaveRepository.deleteById(id);
    }

    /// /////// Custom //////////

    @Transactional(readOnly = true)
    public List<Object[]> findMonthWithMostSickLeaves() {
        return sickLeaveRepository.findMonthWithMostSickLeaves(PageRequest.of(0, 1));
    }

    @Transactional(readOnly = true)
    public List<Object[]> findDoctorsWithMostSickLeaves() {
        return sickLeaveRepository.findDoctorsWithMostSickLeaves(PageRequest.of(0, 1));
    }
}
