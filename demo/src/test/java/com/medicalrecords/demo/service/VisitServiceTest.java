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
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTest {
    @Mock
    private VisitMapper visitMapper;
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private DiagnosisMapper diagnosisMapper;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private VisitService visitService;

    private VisitEntity visitEntity;
    private VisitDTO visitDTO;
    private AppUserEntity adminUser;
    private AppUserEntity doctorUser;
    private AppUserEntity patientUser;
    private DoctorEntity doctor;
    private PatientEntity patient;

    @BeforeEach
    void setUp() {
        visitEntity = new VisitEntity();
        visitEntity.setId(1L);

        visitDTO = new VisitDTO();
        visitDTO.setId(null);
        visitDTO.setDoctorId(1L);

        adminUser = new AppUserEntity();
        adminUser.setRole(AppUserEntity.Role.ADMIN);

        doctorUser = new AppUserEntity();
        doctorUser.setRole(AppUserEntity.Role.DOCTOR);

        patientUser = new AppUserEntity();
        patientUser.setRole(AppUserEntity.Role.PATIENT);

        doctor = new DoctorEntity();
        doctor.setId(1L);

        patient = new PatientEntity();
        patient.setId(1L);
    }


    /// /////// upsert() //////////

    @Test
    void upsert_asAdmin_create_saves() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(visitMapper.toEntity(visitDTO)).thenReturn(visitEntity);

        visitService.upsert(visitDTO);

        verify(visitRepository).save(visitEntity);
    }

    @Test
    void upsert_asDoctor_ownVisit_saves() {
        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);
        when(securityUtils.getCurrentDoctor()).thenReturn(doctor);
        when(visitMapper.toEntity(visitDTO)).thenReturn(visitEntity);

        visitService.upsert(visitDTO);

        verify(visitRepository).save(visitEntity);
    }

    @Test
    void upsert_asDoctor_otherDoctorId_throwsAccessDenied() {
        visitDTO.setDoctorId(99L);

        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);
        when(securityUtils.getCurrentDoctor()).thenReturn(doctor);

        assertThatThrownBy(() -> visitService.upsert(visitDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You can only create/edit your own visits");
    }

    @Test
    void upsert_update_updatesExistingEntity() {
        visitDTO.setId(1L);
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visitEntity));

        visitService.upsert(visitDTO);

        verify(visitMapper).updateEntityFromDto(visitEntity, visitDTO);
        verify(visitRepository).save(visitEntity);
    }

    @Test
    void upsert_update_throwsWhenNotFound() {
        visitDTO.setId(1L);
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.upsert(visitDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Visit not found");
    }


    /// /////// readById() //////////

    @Test
    void readById_returnsDto() {
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visitEntity));
        when(visitMapper.toDto(visitEntity)).thenReturn(visitDTO);

        VisitDTO result = visitService.readById(1L);

        assertThat(result).isEqualTo(visitDTO);
    }

    @Test
    void readById_throwsWhenNotFound() {
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.readById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Visit not found");
    }


    /// /////// readAll() //////////

    @Test
    void readAll_returnsDtoList() {
        when(visitRepository.findAll()).thenReturn(List.of(visitEntity));
        when(visitMapper.toDtoList(List.of(visitEntity))).thenReturn(List.of(visitDTO));

        List<VisitDTO> result = visitService.readAll();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(visitDTO);
    }


    /// /////// delete() //////////

    @Test
    void delete_asAdmin_deletesSuccessfully() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);

        visitService.delete(1L);

        verify(visitRepository).deleteById(1L);
    }

    @Test
    void delete_asDoctor_throwsAccessDenied() {
        when(securityUtils.getCurrentUser()).thenReturn(doctorUser);

        assertThatThrownBy(() -> visitService.delete(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only admin can delete visits");
    }

    @Test
    void delete_asPatient_throwsAccessDenied() {
        when(securityUtils.getCurrentUser()).thenReturn(patientUser);

        assertThatThrownBy(() -> visitService.delete(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only admin can delete visits");
    }


    /// /////// custom queries //////////

    @Test
    void findByPatientId_asAdmin_returnsList() {
        List<VisitEntity> entities = new ArrayList<>();
        entities.add(visitEntity);
        List<VisitDTO> dtos = new ArrayList<>();
        dtos.add(visitDTO);

        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(visitRepository.findByPatientId(1L)).thenReturn(entities);
        when(visitMapper.toDtoList(entities)).thenReturn(dtos);

        List<VisitDTO> result = visitService.findByPatientId(1L);

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(visitDTO);
    }

    @Test
    void findByPatientId_asPatient_ownId_returnsList() {
        List<VisitEntity> entities = new ArrayList<>();
        entities.add(visitEntity);
        List<VisitDTO> dtos = new ArrayList<>();
        dtos.add(visitDTO);

        when(securityUtils.getCurrentUser()).thenReturn(patientUser);
        when(securityUtils.getCurrentPatient()).thenReturn(patient);
        when(visitRepository.findByPatientId(1L)).thenReturn(entities);
        when(visitMapper.toDtoList(entities)).thenReturn(dtos);

        List<VisitDTO> result = visitService.findByPatientId(1L);

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(visitDTO);
    }

    @Test
    void findByPatientId_asPatient_otherId_throwsAccessDenied() {
        PatientEntity otherPatient = new PatientEntity();
        otherPatient.setId(99L);

        when(securityUtils.getCurrentUser()).thenReturn(patientUser);
        when(securityUtils.getCurrentPatient()).thenReturn(otherPatient);

        assertThatThrownBy(() -> visitService.findByPatientId(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    void findByDoctorId_returnsList() {
        List<VisitEntity> entities = new ArrayList<>();
        entities.add(visitEntity);
        List<VisitDTO> dtos = new ArrayList<>();
        dtos.add(visitDTO);

        when(visitRepository.findByDoctorId(1L)).thenReturn(entities);
        when(visitMapper.toDtoList(entities)).thenReturn(dtos);

        List<VisitDTO> result = visitService.findByDoctorId(1L);

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(visitDTO);
    }

    @Test
    void findByDoctorAndPeriod_returnsList() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        List<VisitEntity> entities = new ArrayList<>();
        entities.add(visitEntity);
        List<VisitDTO> dtos = new ArrayList<>();
        dtos.add(visitDTO);

        when(visitRepository.findByDoctorAndPeriod(1L, from, to)).thenReturn(entities);
        when(visitMapper.toDtoList(entities)).thenReturn(dtos);

        List<VisitDTO> result = visitService.findByDoctorAndPeriod(1L, from, to);

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly(visitDTO);
    }

    @Test
    void totalPaidByPatients_returnsAmount() {
        when(visitRepository.totalPaidByPatients()).thenReturn(new BigDecimal("1500.00"));

        BigDecimal result = visitService.totalPaidByPatients();

        assertThat(result).isEqualByComparingTo("1500.00");
    }

    @Test
    void totalPaidByPatientsPerDoctor_returnsRawList() {
        Object[] row = {1L, "Dr. D", new BigDecimal("500.00")};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);
        when(visitRepository.totalPaidByPatientsPerDoctor()).thenReturn(mockResult);

        List<Object[]> result = visitService.totalPaidByPatientsPerDoctor();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
    }

    @Test
    void countVisitsPerDoctor_returnsRawList() {
        Object[] row = {1L, "Dr. D", 10L};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);
        when(visitRepository.countVisitsPerDoctor()).thenReturn(mockResult);

        List<Object[]> result = visitService.countVisitsPerDoctor();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
    }

    @Test
    void findMostCommonDiagnosis_returnsDto() {
        DiagnosisEntity diagnosisEntity = new DiagnosisEntity();
        diagnosisEntity.setId(1L);
        DiagnosisDTO diagnosisDTO = new DiagnosisDTO();
        diagnosisDTO.setId(1L);

        when(visitRepository.findMostCommonDiagnosis(PageRequest.of(0, 1)))
                .thenReturn(List.of(diagnosisEntity));
        when(diagnosisMapper.toDto(diagnosisEntity)).thenReturn(diagnosisDTO);

        DiagnosisDTO result = visitService.findMostCommonDiagnosis();

        assertThat(result).isEqualTo(diagnosisDTO);
    }

    @Test
    void findMostCommonDiagnosis_throwsWhenNoVisits() {
        when(visitRepository.findMostCommonDiagnosis(PageRequest.of(0, 1)))
                .thenReturn(List.of());

        assertThatThrownBy(() -> visitService.findMostCommonDiagnosis())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No diagnoses found");
    }
}
