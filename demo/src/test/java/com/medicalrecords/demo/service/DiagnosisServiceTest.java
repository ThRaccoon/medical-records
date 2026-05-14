package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.DiagnosisDTO;
import com.medicalrecords.demo.entity.DiagnosisEntity;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.DiagnosisMapper;
import com.medicalrecords.demo.repository.DiagnosisRepository;
import com.medicalrecords.demo.security.SecurityUtils;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiagnosisServiceTest {
    @Mock
    private DiagnosisMapper diagnosisMapper;
    @Mock
    private DiagnosisRepository diagnosisRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private DiagnosisService diagnosisService;

    private DiagnosisEntity entity;
    private DiagnosisDTO dto;

    @BeforeEach
    void setUp() {
        entity = new DiagnosisEntity();
        entity.setId(1L);

        dto = new DiagnosisDTO();
        dto.setId(1L);
    }


    /// /////// upsert() //////////

    @Test
    void upsert_create_savesNewEntity() {
        dto.setId(null);
        when(diagnosisMapper.toEntity(dto)).thenReturn(entity);

        diagnosisService.upsert(dto);

        verify(diagnosisRepository).save(entity);

    }

    @Test
    void upsert_update_updatesExistingEntity() {
        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(entity));

        diagnosisService.upsert(dto);

        verify(diagnosisMapper).updateEntityFromDto(entity, dto);
        verify(diagnosisRepository).save(entity);
    }

    @Test
    void upsert_update_throwsWhenNotFound() {
        when(diagnosisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> diagnosisService.upsert(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Diagnosis not found");
    }


    /// /////// readById() //////////

    @Test
    void readById_returnsDto() {
        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(diagnosisMapper.toDto(entity)).thenReturn(dto);

        DiagnosisDTO result = diagnosisService.readById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void readById_throwsWhenNotFound() {
        when(diagnosisRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> diagnosisService.readById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Diagnosis not found");
    }


    /// /////// readAll() //////////

    @Test
    void readAll_returnsDtoList() {
        when(securityUtils.isPatient()).thenReturn(false);
        when(diagnosisRepository.findAll()).thenReturn(List.of(entity));
        when(diagnosisMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<DiagnosisDTO> result = diagnosisService.readAll();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(dto);
    }

    /// /////// delete() //////////

    @Test
    void delete_callsRepositoryDeleteById() {
        diagnosisService.delete(1L);

        verify(diagnosisRepository).deleteById(1L);
    }
}
