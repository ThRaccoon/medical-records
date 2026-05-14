package com.medicalrecords.demo.controller;

import com.medicalrecords.demo.dto.VisitDTO;
import com.medicalrecords.demo.entity.AppUserEntity;
import com.medicalrecords.demo.entity.DoctorEntity;
import com.medicalrecords.demo.entity.PatientEntity;
import com.medicalrecords.demo.exceptions.AccessDeniedException;
import com.medicalrecords.demo.security.SecurityUtils;
import com.medicalrecords.demo.service.DiagnosisService;
import com.medicalrecords.demo.service.DoctorService;
import com.medicalrecords.demo.service.PatientService;
import com.medicalrecords.demo.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("visits", visitService.readAll());
        return "visits/list";
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public String myVisits(Model model) {
        PatientEntity currentPatient = securityUtils.getCurrentPatient();
        model.addAttribute("visits", visitService.findByPatientId(currentPatient.getId()));
        return "visits/my";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newForm(Model model) {
        model.addAttribute("visit", new VisitDTO());
        model.addAttribute("doctors", doctorService.readAll());
        model.addAttribute("patients", patientService.readAll());
        model.addAttribute("diagnoses", diagnosisService.readAll());
        return "visits/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        VisitDTO visit = visitService.readById(id);

        AppUserEntity currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == AppUserEntity.Role.DOCTOR) {
            DoctorEntity currentDoctor = securityUtils.getCurrentDoctor();
            if (!visit.getDoctorId().equals(currentDoctor.getId())) {
                throw new AccessDeniedException("You can only edit your own visits.");
            }
        }

        model.addAttribute("visit", visit);
        model.addAttribute("doctors", doctorService.readAll());
        model.addAttribute("patients", patientService.readAll());
        model.addAttribute("diagnoses", diagnosisService.readAll());
        return "visits/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String save(@ModelAttribute("visit") @Valid VisitDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("visit", dto);
            model.addAttribute("doctors", doctorService.readAll());
            model.addAttribute("patients", patientService.readAll());
            model.addAttribute("diagnoses", diagnosisService.readAll());
            return "visits/form";
        }
        AppUserEntity currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == AppUserEntity.Role.DOCTOR) {
            DoctorEntity currentDoctor = securityUtils.getCurrentDoctor();
            if (!dto.getDoctorId().equals(currentDoctor.getId())) {
                throw new AccessDeniedException("You can only save your own visits.");
            }
        }
        visitService.upsert(dto);
        return "redirect:/visits";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        visitService.delete(id);
        return "redirect:/visits";
    }
}
