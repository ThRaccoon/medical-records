package com.medicalrecords.demo.security;

import com.medicalrecords.demo.entity.AppUserEntity;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.entity.PatientEntity;
import com.medicalrecords.demo.exceptions.AccessDeniedException;
import com.medicalrecords.demo.exceptions.EntityNotFoundException;
import com.medicalrecords.demo.repository.AppUserRepository;
import com.medicalrecords.demo.repository.DoctorRepository;
import com.medicalrecords.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final AppUserRepository appUserRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppUserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No authenticated user found");
        }
        String username = authentication.getName();
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public PatientEntity getCurrentPatient() {
        AppUserEntity user = getCurrentUser();
        return patientRepository.findByAppUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found for current user"));
    }

    public DoctorEntity getCurrentDoctor() {
        AppUserEntity user = getCurrentUser();
        return doctorRepository.findByAppUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found for current user"));
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == AppUserEntity.Role.ADMIN;
    }

    public boolean isDoctor() {
        return getCurrentUser().getRole() == AppUserEntity.Role.DOCTOR;
    }

    public boolean isPatient() {
        return getCurrentUser().getRole() == AppUserEntity.Role.PATIENT;
    }
}
