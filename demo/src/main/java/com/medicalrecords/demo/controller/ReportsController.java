package com.medicalrecords.demo.controller;

import com.medicalrecords.demo.entity.AppUserEntity;
import com.medicalrecords.demo.entity.PatientEntity;
import com.medicalrecords.demo.security.SecurityUtils;
import com.medicalrecords.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {
    private final PatientService patientService;
    private final VisitService visitService;
    private final SickLeaveService sickLeaveService;
    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String index(Model model) {
        model.addAttribute("doctors", doctorService.readAll());
        model.addAttribute("diagnoses", diagnosisService.readAll());
        model.addAttribute("patients", patientService.readAll());
        return "reports/index";
    }

    @GetMapping("/patients-by-diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String patientsByDiagnosis(@RequestParam Long diagnosisId, Model model) {
        model.addAttribute("patients", patientService.findByDiagnosisId(diagnosisId));
        model.addAttribute("diagnoses", diagnosisService.readAll());
        model.addAttribute("selectedDiagnosisId", diagnosisId);
        return "reports/patients-by-diagnosis";
    }

    @GetMapping("/patients-by-gp")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String patientsByGp(@RequestParam Long gpDoctorId, Model model) {
        model.addAttribute("patients", patientService.findByGpDoctorId(gpDoctorId));
        model.addAttribute("doctors", doctorService.readAll());
        model.addAttribute("selectedDoctorId", gpDoctorId);
        return "reports/patients-by-gp";
    }

    @GetMapping("/count-per-gp")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String countPerGp(Model model) {
        model.addAttribute("data", patientService.countPatientsPerGp());
        return "reports/count-per-gp";
    }

    @GetMapping("/most-common-diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String mostCommonDiagnosis(Model model) {
        model.addAttribute("diagnosis", visitService.findMostCommonDiagnosis());
        return "reports/most-common-diagnosis";
    }

    @GetMapping("/total-paid-by-patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String totalPaidByPatients(Model model) {
        model.addAttribute("total", visitService.totalPaidByPatients());
        return "reports/total-paid-by-patients";
    }

    @GetMapping("/total-paid-per-doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String totalPaidPerDoctor(Model model) {
        model.addAttribute("data", visitService.totalPaidByPatientsPerDoctor());
        return "reports/total-paid-per-doctor";
    }

    @GetMapping("/count-per-doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String countPerDoctor(Model model) {
        model.addAttribute("data", visitService.countVisitsPerDoctor());
        return "reports/count-per-doctor";
    }

    @GetMapping("/visits-by-patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public String visitsByPatient(@RequestParam(required = false) Long patientId, Model model) {
        AppUserEntity currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == AppUserEntity.Role.PATIENT) {
            PatientEntity currentPatient = securityUtils.getCurrentPatient();
            patientId = currentPatient.getId();
        }

        if (patientId == null) {
            model.addAttribute("visits", List.of());
        } else {
            model.addAttribute("visits", visitService.findByPatientId(patientId));
        }

        if (currentUser.getRole() != AppUserEntity.Role.PATIENT) {
            model.addAttribute("patients", patientService.readAll());
        }

        model.addAttribute("selectedPatientId", patientId);
        return "reports/visits-by-patient";
    }

    @GetMapping("/visits-filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String visitsFilter(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        model.addAttribute("visits", visitService.findByDoctorAndPeriod(doctorId, from, to));
        model.addAttribute("doctors", doctorService.readAll());
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "reports/visits-filter";
    }

    @GetMapping("/month-with-most-sick-leaves")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String monthWithMostSickLeaves(Model model) {
        model.addAttribute("data", sickLeaveService.findMonthWithMostSickLeaves());
        return "reports/month-with-most-sick-leaves";
    }

    @GetMapping("/doctors-with-most-sick-leaves")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String doctorsWithMostSickLeaves(Model model) {
        model.addAttribute("data", sickLeaveService.findDoctorsWithMostSickLeaves());
        return "reports/doctors-with-most-sick-leaves";
    }
}
