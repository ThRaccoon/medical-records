package com.medicalrecords.demo.service;

import com.medicalrecords.demo.dto.DoctorDTO;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.mapper.DoctorMapper;
import com.medicalrecords.demo.repository.DoctorRepository;
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
public class DoctorServiceTest {
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private DoctorEntity entity;
    private DoctorDTO dto;

    @BeforeEach
    void setUp() {
        entity = new DoctorEntity();
        entity.setId(1L);

        dto = new DoctorDTO();
        dto.setId(1L);
    }


    /// /////// upsert() //////////

    @Test
    void upsert_create_savesNewEntity() {
        dto.setId(null);
        when(doctorMapper.toEntity(dto)).thenReturn(entity);

        doctorService.upsert(dto);

        verify(doctorRepository).save(entity);
    }

    @Test
    void upsert_update_updatesExistingEntity() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(entity));

        doctorService.upsert(dto);

        verify(doctorMapper).updateEntityFromDto(entity, dto);
        verify(doctorRepository).save(entity);
    }

    @Test
    void upsert_update_throwsWhenNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.upsert(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Doctor not found");
    }


    /// /////// readById() //////////

    @Test
    void readById_returnsDto() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(doctorMapper.toDto(entity)).thenReturn(dto);

        DoctorDTO result = doctorService.readById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void readById_throwsWhenNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.readById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Doctor not found");
    }


    /// /////// readAll() //////////

    @Test
    void readAll_returnsDtoList() {
        when(doctorRepository.findAll()).thenReturn(List.of(entity));
        when(doctorMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        List<DoctorDTO> result = doctorService.readAll();

        assertThat(result).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1).containsExactly(dto);
    }


    /// /////// delete() //////////

    @Test
    void delete_callsRepositoryDeleteById() {
        doctorService.delete(1L);

        verify(doctorRepository).deleteById(1L);
    }
}
