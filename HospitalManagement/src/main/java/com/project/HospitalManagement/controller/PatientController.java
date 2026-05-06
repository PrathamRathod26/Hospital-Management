package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@Tag(name="Patient APIs")
@Slf4j
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/")
    @Operation(summary = "Get Patient by User Id")
    public ResponseEntity<PatientDto.response> getPatientByUserId(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(patientService.getPatientByUserId(user.getId()));
    }

    @GetMapping("/view/details/v1")
    @Operation(summary = "Get Patient details")
    public ResponseEntity<PatientDto.patientDetailsWithDocumentData> getPatientDetailsV1(
            @RequestParam("patientId") Long patientId
    ){
        return ResponseEntity.ok(patientService.getPatientDetailsV1(patientId));
    }

    @GetMapping("/view/details/v2")
    @Operation(summary = "Get Patient details")
    public ResponseEntity<PatientDto.patientDetailsWithDocumentDataAndAppointmentData> getPatientDetailsV2(
            @RequestParam("patientId") Long patientId,
            @RequestParam("appointmentId") Long appointmentId
    ){
        return ResponseEntity.ok(patientService.getPatientDetailsV2(patientId, appointmentId));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Patient Profile")
    public ResponseEntity<String> getPatientProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(patientService.getPatientProfileDetails(user.getId()));
    }
}
