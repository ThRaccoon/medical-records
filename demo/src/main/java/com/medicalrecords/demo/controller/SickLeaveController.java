package com.medicalrecords.demo.controller;

import com.medicalrecords.demo.dto.SickLeaveDTO;
import com.medicalrecords.demo.service.SickLeaveService;
import com.medicalrecords.demo.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveController {
    private final SickLeaveService sickLeaveService;
    private final VisitService visitService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("sickLeaves", sickLeaveService.readAll());
        return "sick-leaves/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newForm(Model model) {
        model.addAttribute("sickLeave", new SickLeaveDTO());
        model.addAttribute("visits", visitService.readAll());
        return "sick-leaves/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("sickLeave", sickLeaveService.readById(id));
        model.addAttribute("visits", visitService.readAll());
        return "sick-leaves/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String save(@ModelAttribute("sickLeave") @Valid SickLeaveDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sickLeave", dto);
            model.addAttribute("visits", visitService.readAll());
            return "sick-leaves/form";
        }
        sickLeaveService.upsert(dto);
        return "redirect:/sick-leaves";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        sickLeaveService.delete(id);
        return "redirect:/sick-leaves";
    }
}
