package com.medicalrecords.demo.controller;

import com.medicalrecords.demo.dto.DoctorDTO;
import com.medicalrecords.demo.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("doctors", doctorService.readAll());
        return "doctors/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("doctor", new DoctorDTO());
        return "doctors/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.readById(id));
        return "doctors/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute("doctor") @Valid DoctorDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctor", dto);
            return "doctors/form";
        }
        doctorService.upsert(dto);
        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        doctorService.delete(id);
        return "redirect:/doctors";
    }
}
