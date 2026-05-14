package com.medicalrecords.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "EGN is required")
    @Pattern(regexp = "\\d{10}", message = "EGN must be exactly 10 digits")
    private String egn;

    @NotNull(message = "GP Doctor is required")
    private Long gpDoctorId; // Needed for UI

    private boolean insured;
}
