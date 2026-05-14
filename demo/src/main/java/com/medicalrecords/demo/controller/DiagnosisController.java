package com.medicalrecords.demo.controller;

import com.medicalrecords.demo.dto.DiagnosisDTO;
import com.medicalrecords.demo.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public String list(Model model) {
        model.addAttribute("diagnoses", diagnosisService.readAll());
        return "diagnoses/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("diagnosis", new DiagnosisDTO());
        return "diagnoses/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("diagnosis", diagnosisService.readById(id));
        return "diagnoses/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute("diagnosis") @Valid DiagnosisDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("diagnosis", dto);
            return "diagnoses/form";
        }
        diagnosisService.upsert(dto);
        return "redirect:/diagnoses";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        diagnosisService.delete(id);
        return "redirect:/diagnoses";
    }
}
