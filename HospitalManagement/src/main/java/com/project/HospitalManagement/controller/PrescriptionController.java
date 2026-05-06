package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.PrescriptionDto;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription")
@Tag(name="Prescription APIs")
@Slf4j
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/doctor/add")
    @Operation(summary = "Add prescription on appointment")
    public ResponseEntity<PrescriptionDto.response> addPrescription(
        @RequestParam(required = true) Long appointmentId,
        @RequestBody PrescriptionDto.request dto,
        @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(prescriptionService.addPrescription(user.getId(), appointmentId,dto));
    }

    @GetMapping("/")
    @Operation(summary = "Get prescription")
    public ResponseEntity<PrescriptionDto.response> getPrescription(
            @RequestParam(required = true) Long appointmentId,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(prescriptionService.getPrescriptionAsPatient(user.getId(), appointmentId));
    }
}
