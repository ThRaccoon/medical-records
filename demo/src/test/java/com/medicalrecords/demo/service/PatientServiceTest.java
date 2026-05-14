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
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PatientService patientService;

    private PatientEntity patientEntity;
    private PatientDTO patientDTO;
    private AppUserEntity adminUser;
    private AppUserEntity patientUser;

    @BeforeEach
    void setUp() {
        patientEntity = new PatientEntity();
        patientEntity.setId(1L);

        patientDTO = new PatientDTO();
        patientDTO.setId(1L);

        adminUser = new AppUserEntity();
        adminUser.setRole(AppUserEntity.Role.ADMIN);

        patientUser = new AppUserEntity();
        patientUser.setRole(AppUserEntity.Role.PATIENT);
    }


    /// /////// upsert() //////////

    @Test
    void upsert_create_savesNewEntity() {
        patientDTO.setId(null);
        patientDTO.setGpDoctorId(1L);
        DoctorEntity doctor = new DoctorEntity();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientMapper.toEntity(patientDTO)).thenReturn(patientEntity);

        patientService.upsert(patientDTO);

        verify(patientRepository).save(patientEntity);
    }

    @Test
    void upsert_update_updatesExistingEntity() {
        patientDTO.setGpDoctorId(1L);
        DoctorEntity doctor = new DoctorEntity();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));

        patientService.upsert(patientDTO);

        verify(patientMapper).updateEntityFromDto(patientEntity, patientDTO);
        verify(patientRepository).save(patientEntity);
    }

    @Test
    void upsert_update_throwsWhenNotFound() {
        patientDTO.setGpDoctorId(1L);
        DoctorEntity doctor = new DoctorEntity();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.upsert(patientDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Patient not found");
    }


    /// /////// readById() //////////

    @Test
    void readById_asAdmin_returnsDto() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));
        when(patientMapper.toDto(patientEntity)).thenReturn(patientDTO);

        PatientDTO result = patientService.readById(1L);

        assertThat(result).isEqualTo(patientDTO);
    }

    @Test
    void readById_asPatient_ownRecord_returnsDto() {
        when(securityUtils.getCurrentUser()).thenReturn(patientUser);
        when(securityUtils.getCurrentPatient()).thenReturn(patientEntity);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));
        when(patientMapper.toDto(patientEntity)).thenReturn(patientDTO);

        PatientDTO result = patientService.readById(1L);

        assertThat(result).isEqualTo(patientDTO);
    }

    @Test
    void readById_asPatient_otherRecord_throwsAccessDenied() {
        PatientEntity otherPatient = new PatientEntity();
        otherPatient.setId(99L);

        when(securityUtils.getCurrentUser()).thenReturn(patientUser);
        when(securityUtils.getCurrentPatient()).thenReturn(otherPatient);

        assertThatThrownBy(() -> patientService.readById(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    void readById_throwsWhenNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.readById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Patient not found");
    }


    /// /////// readAll() //////////

    @Test
    void readAll_asAdmin_returnsList() {
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(patientRepository.findAll()).thenReturn(List.of(patientEntity));
        when(patientMapper.toDtoList(List.of(patientEntity))).thenReturn(List.of(patientDTO));

        List<PatientDTO> result = patientService.readAll();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(patientDTO);
    }

    @Test
    void readAll_asPatient_throwsAccessDenied() {
        when(securityUtils.getCurrentUser()).thenReturn(patientUser);

        assertThatThrownBy(() -> patientService.readAll())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }


    /// /////// delete() //////////

    @Test
    void delete_callsRepositoryDeleteById() {
        patientService.delete(1L);

        verify(patientRepository).deleteById(1L);
    }


    /// /////// custom queries //////////

    @Test
    void findByDiagnosisId_returnsMappedList() {
        when(patientRepository.findByDiagnosisId(5L)).thenReturn(List.of(patientEntity));
        when(patientMapper.toDtoList(List.of(patientEntity))).thenReturn(List.of(patientDTO));

        List<PatientDTO> result = patientService.findByDiagnosisId(5L);

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(patientDTO);
    }

    @Test
    void findByGpDoctorId_returnsMappedList() {
        when(patientRepository.findByGpDoctorId(2L)).thenReturn(List.of(patientEntity));
        when(patientMapper.toDtoList(List.of(patientEntity))).thenReturn(List.of(patientDTO));

        List<PatientDTO> result = patientService.findByGpDoctorId(2L);

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(patientDTO);
    }

    @Test
    void countPatientsPerGp_returnsRawList() {
        Object[] row = {1L, 3L};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);
        when(patientRepository.countPatientsPerGp()).thenReturn(mockResult);

        List<Object[]> result = patientService.countPatientsPerGp();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
    }
}
