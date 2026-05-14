package com.medicalrecords.demo.controller;

import com.medicalrecords.demo.dto.PatientDTO;
import com.medicalrecords.demo.service.DoctorService;
import com.medicalrecords.demo.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("patients", patientService.readAll());
        return "patients/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("patient", new PatientDTO());
        model.addAttribute("doctors", doctorService.readAll());
        return "patients/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.readById(id));
        model.addAttribute("doctors", doctorService.readAll());
        return "patients/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute("patient") @Valid PatientDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("patient", dto);
            model.addAttribute("doctors", doctorService.readAll());
            return "patients/form";
        }
        patientService.upsert(dto);
        return "redirect:/patients";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        patientService.delete(id);
        return "redirect:/patients";
    }
}
