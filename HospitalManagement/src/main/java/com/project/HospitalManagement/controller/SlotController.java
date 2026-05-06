package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.SlotDto;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slot")
@Tag(name="Slot APIs")
@Slf4j
public class SlotController {
    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/doctor/generate")
    @Operation(summary = "Generate Slots for all doctors")
    public ResponseEntity<Void> generateSlots(){
        slotService.generateSlotForAllDoctors();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/doctor/add")
    @Operation(summary = "Add a slot for doctor")
    public ResponseEntity<SlotDto.response> addSlot(
            @AuthenticationPrincipal User user,
            @RequestBody SlotDto.request dto
    ){
        return ResponseEntity.ok(slotService.makeSlot(user.getId(), dto));
    }

    @GetMapping("/public/filter")
    @Operation(summary = "get slots")
    public ResponseEntity<String> getSlots(
            @RequestParam(required = false) long doctorId,
            @RequestParam(required = false) LocalDate date
    ){
        return ResponseEntity.ok(slotService.getSlots(doctorId,date));
    }

    @GetMapping("/public/filter/v3")
    @Operation(summary = "get slots")
    public ResponseEntity<List<SlotDto.response>> getSlotsV3(
            @RequestParam(required = false) long doctorId,
            @RequestParam(required = false) LocalDate date
    ){
        return ResponseEntity.ok(slotService.getSlotsV3(doctorId,date));
    }

    @GetMapping("/patient/filter/user")
    @Operation(summary = "get slots")
    public ResponseEntity<List<SlotDto.responseWithAppointmentIdFlag>> getSlots(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) LocalDate date
    ){
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(slotService.getSlotsByUser(user.getId(), doctorId,date));
    }

    @GetMapping("/doctor/doctor")
    @Operation(summary = "get all slots by doctor")
    public ResponseEntity<List<SlotDto.response>> getAllDoctorSlots(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(slotService.getAllDoctorSlotsByUserId(user.getId()));
    }

    @PutMapping("/doctor/disable")
    @Operation(summary = "Disable slot")
    public ResponseEntity<Void> disableSlot(
            @AuthenticationPrincipal User user,
            @RequestParam(required = true) Long slotId
    ){
        slotService.disableSlot(user.getId(), slotId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/doctor/update")
    @Operation(summary = "Update slot")
    public ResponseEntity<SlotDto.response> updateSlot(
            @AuthenticationPrincipal User user,
            @RequestBody SlotDto.request dto
            ){
        return ResponseEntity.ok(slotService.updateSlot(user.getId(), dto));
    }
}
