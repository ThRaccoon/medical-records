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
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SickLeaveServiceTest {
    @Mock
    private SickLeaveMapper sickLeaveMapper;
    @Mock
    private SickLeaveRepository sickLeaveRepository;
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private SickLeaveService sickLeaveService;

    private SickLeaveEntity sickLeaveEntity;
    private SickLeaveDTO sickLeaveDTO;
    private AppUserEntity adminUser;
    private AppUserEntity doctorUser;
    private AppUserEntity patientUser;
    private DoctorEntity doctor;
    private VisitEntity visit;

    @BeforeEach
    void setUp() {
        sickLeaveEntity = new SickLeaveEntity();
        sickLeaveEntity.setId(1L);

        sickLeaveDTO = new SickLeaveDTO();
        sickLeaveDTO.setId(null);
        sickLeaveDTO.setVisitId(10L);

        adminUser = new AppUserEntity();
        adminUser.setRole(AppUserEntity.Role.ADMIN);

        doctorUser = new AppUserEntity();
        doctorUser.setRole(AppUserEntity.Role.DOCTOR);

        patientUser = new AppUserEntity();
        patientUser.setRole(AppUserEntity.Role.PATIENT);

        doctor = new DoctorEntity();
        doctor.setId(1L);

        DoctorEntity visitDoctor = new DoctorEntity();
        visitDoctor.setId(1L);

        visit = new VisitEntity();
        visit.setId(10L);
        visit.setDoctor(visitDoctor);
    }


    /// /////// upsert() //////////

    @Test
    void upsert_asAdmin_create_saves() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(sickLeaveMapper.toEntity(sickLeaveDTO)).thenReturn(sickLeaveEntity);

        sickLeaveService.upsert(sickLeaveDTO);

        verify(sickLeaveRepository).save(sickLeaveEntity);
    }

    @Test
    void upsert_asDoctor_ownVisit_saves() {
        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);
        when(securityUtils.getCurrentDoctor()).thenReturn(doctor);
        when(visitRepository.findById(10L)).thenReturn(Optional.of(visit));
        when(sickLeaveMapper.toEntity(sickLeaveDTO)).thenReturn(sickLeaveEntity);

        sickLeaveService.upsert(sickLeaveDTO);

        verify(sickLeaveRepository).save(sickLeaveEntity);
    }

    @Test
    void upsert_asDoctor_otherVisit_throwsAccessDenied() {
        DoctorEntity otherDoctor = new DoctorEntity();
        otherDoctor.setId(99L);

        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);
        when(securityUtils.getCurrentDoctor()).thenReturn(otherDoctor);
        when(visitRepository.findById(10L)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> sickLeaveService.upsert(sickLeaveDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can only create sick leaves for your own visits");
    }

    @Test
    void upsert_asDoctor_visitNotFound_throwsEntityNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);
        when(securityUtils.getCurrentDoctor()).thenReturn(doctor);
        when(visitRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sickLeaveService.upsert(sickLeaveDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Visit not found");
    }

    @Test
    void upsert_update_updatesExistingEntity() {
        sickLeaveDTO.setId(1L);
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeaveEntity));

        sickLeaveService.upsert(sickLeaveDTO);

        verify(sickLeaveMapper).updateEntityFromDto(sickLeaveEntity, sickLeaveDTO);
        verify(sickLeaveRepository).save(sickLeaveEntity);
    }

    @Test
    void upsert_update_throwsWhenNotFound() {
        sickLeaveDTO.setId(1L);
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sickLeaveService.upsert(sickLeaveDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Sick leave not found");
    }


    /// /////// readById() //////////

    @Test
    void readById_returnsDto() {
        SickLeaveDTO dto = new SickLeaveDTO();
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sickLeaveEntity));
        when(sickLeaveMapper.toDto(sickLeaveEntity)).thenReturn(dto);

        SickLeaveDTO result = sickLeaveService.readById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void readById_throwsWhenNotFound() {
        when(sickLeaveRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sickLeaveService.readById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Sick leave not found");
    }


    /// /////// readAll() //////////

    @Test
    void readAll_returnsDtoList() {
        SickLeaveDTO dto = new SickLeaveDTO();
        when(sickLeaveRepository.findAll()).thenReturn(List.of(sickLeaveEntity));
        when(sickLeaveMapper.toDtoList(List.of(sickLeaveEntity))).thenReturn(List.of(dto));

        List<SickLeaveDTO> result = sickLeaveService.readAll();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(dto);
    }


    /// /////// delete() //////////

    @Test
    void delete_asAdmin_deletesSuccessfully() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);

        sickLeaveService.delete(1L);

        verify(sickLeaveRepository).deleteById(1L);
    }

    @Test
    void delete_asDoctor_throwsAccessDenied() {
        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);

        assertThatThrownBy(() -> sickLeaveService.delete(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only admin can delete sick leaves");
    }

    @Test
    void delete_asPatient_throwsAccessDenied() {
        when(securityUtils.getCurrentUser()).thenReturn(patientUser);

        assertThatThrownBy(() -> sickLeaveService.delete(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only admin can delete sick leaves");
    }


    /// /////// custom queries //////////

    @Test
    void findMonthWithMostSickLeaves_returnsResult() {
        Object[] row = {"2026-03", 5L};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);
        when(sickLeaveRepository.findMonthWithMostSickLeaves(PageRequest.of(0, 1)))
                .thenReturn(mockResult);

        List<Object[]> result = sickLeaveService.findMonthWithMostSickLeaves();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
    }

    @Test
    void findDoctorsWithMostSickLeaves_returnsResult() {
        Object[] row = {1L, "Dr. D", 10L};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);
        when(sickLeaveRepository.findDoctorsWithMostSickLeaves(PageRequest.of(0, 1)))
                .thenReturn(mockResult);

        List<Object[]> result = sickLeaveService.findDoctorsWithMostSickLeaves();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
    }
}
