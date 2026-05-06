package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.AppointmentDto;
import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.Records.DocumentDto;
import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@Tag(name="Doctor APIs")
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/patient/add/v1")
    @Operation(summary = "Add Appointment")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointment(@AuthenticationPrincipal User user,
                                                                                             @RequestBody AppointmentDto.request dto){
        return ResponseEntity.ok(appointmentService.makeAppointmentV1(dto, user));
    }

    @PostMapping("/patient/add/v2")
    @Operation(summary = "Add Appointment")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointmentV2(
            @AuthenticationPrincipal User user,
            @RequestBody AppointmentDto.requestWithDocumentList dto
    ){
        return ResponseEntity.ok(appointmentService.makeAppointmentV2(dto, user));
    }

    @PostMapping("/patient/add/v3")
    @Operation(summary = "Add appointment with document upload")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointmentV3(
            @AuthenticationPrincipal User user,
            @RequestPart("data") AppointmentDto.request dto,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents
    ){
        return ResponseEntity.ok(appointmentService.makeAppointmentV3(user,dto,documents));
    }

    @PostMapping("/patient/add/draft/v1")
    @Operation(summary = "Add Appointment Draft V1")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointmentDraftV1(
            @AuthenticationPrincipal User user,
            @RequestBody AppointmentDto.draftRequestV1 dto
    ){
        return ResponseEntity.ok(appointmentService.makeAppointmentDraftV1(dto, user));
    }

    @PostMapping("/patient/add/draft/v2")
    @Operation(summary = "Add Appointment Draft V2")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointmentDraftV2(
            @AuthenticationPrincipal User user,
            @RequestPart(value = "data", required = false) AppointmentDto.draftRequestV2 dto,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents
    ){
        return ResponseEntity.ok(appointmentService.makeAppointmentDraftV2(user,dto,documents));
    }

    @PostMapping("/staff/add/v1")
    @Operation(summary = "Add appointment for new Patient V1")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointmentForNewUserV1(@RequestBody AppointmentDto.newUserRequest dto){
        return ResponseEntity.ok(appointmentService.makeAppointmentForNewUserV1(dto));
    }

    @PostMapping("/staff/add/v2")
    @Operation(summary = "Add appointment for new Patient")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> addAppointmentForNewUserV2(
            @RequestPart("data") AppointmentDto.newUserRequest dto,
            @RequestPart("documents") MultipartFile[] documents
    ){
        return ResponseEntity.ok(appointmentService.makeAppointmentForNewUserV2(documents,dto));
    }

    @GetMapping("/doctor/v1")
    @Operation(summary = "Get doctor appointment V1")
    public ResponseEntity<List<AppointmentDto.responseWithPatientAndSlotAndDoctor>> getDoctorAppointmentsV1(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsV1(user.getId()));
    }

    @GetMapping("/doctor/v2")
    @Operation(summary = "Get doctor appointment V2")
    public ResponseEntity<String> getDoctorAppointmentsV2(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsV2(user.getId()));
    }

    @GetMapping("/doctor/v3")
    @Operation(summary = "Get doctor appointment V3")
    public ResponseEntity<DoctorDto.doctorDetailsAndSlotListWithAppointmentListWithPatient> getDoctorAppointmentsV3(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsV3(user.getId()));
    }

    @GetMapping("/patient/v1")
    public ResponseEntity<List<AppointmentDto.responseWithPatientAndSlotAndDoctor>> getPatientAppointmentsV1(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(appointmentService.getPatientAppointmentsV1(user.getId()));
    }

    @GetMapping("/patient/v2")
    public ResponseEntity<String> getPatientAppointmentsV2(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(appointmentService.getPatientAppointmentsV2(user.getId()));
    }

    @GetMapping("/patient/v3")
    public ResponseEntity<PatientDto.patientDetailsWithAppointmentListWithDoctor> getPatientAppointmentsV3(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(appointmentService.getPatientAppointmentsV3(user.getId()));
    }

    @GetMapping("/patient/appointment")
    @Operation(summary = "Get Draft Appointment by Patient")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> getPatientDraftAppointment(@AuthenticationPrincipal User user, @RequestParam("appointmentId") Long appointmentId){
        return ResponseEntity.ok(appointmentService.getPatientAppointment(user.getId(),appointmentId));
    }

    @GetMapping("/authenticated/documents")
    @Operation(summary = "Get attached Document")
    public ResponseEntity<List<DocumentDto.response>> getAttachedDocumentAsAuthenticated(
            @RequestParam(value = "appointmentId") Long appointmentId
    ){
        return ResponseEntity.ok(appointmentService.getAttachedDocumentAsAuthenticated(appointmentId));
    }

    @DeleteMapping("/patient/remove/document")
    public ResponseEntity<Void> removeDocumentFromDraft(
            @AuthenticationPrincipal User user,
            @RequestParam("appointmentId") Long appointmentId,
            @RequestParam("patientDocumentId") Long patientDocumentId
    ){
        appointmentService.DeleteDocumentFromAppointment(user, appointmentId, patientDocumentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/patient/add/draft-appointment")
    @Operation(summary = "Make appointment from draft")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> makeDraftAppointment(
            @AuthenticationPrincipal User user,
            @RequestPart("data") AppointmentDto.request dto,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents,
            @RequestParam("appointmentId") Long appointmentId
    ){
        return ResponseEntity.ok(appointmentService.makeAppointmentFromDraft(user, appointmentId, dto,documents));
    }

    @PutMapping("/patient/update-draft")
    @Operation(summary = "Update Draft")
    public ResponseEntity<AppointmentDto.responseWithPatientAndSlotAndDoctor> updateDraftAppointment(
            @AuthenticationPrincipal User user,
            @RequestPart("data") AppointmentDto.draftRequestV2 dto,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents,
            @RequestParam("appointmentId") Long appointmentId
    ){
        return ResponseEntity.ok(appointmentService.updateDraftAppointment(user, appointmentId, dto,documents));
    }
}
