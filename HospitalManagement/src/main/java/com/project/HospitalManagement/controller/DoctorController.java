package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@Tag(name="Doctor APIs")
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/")
    @Operation(summary = "Get doctor by user id")
    public ResponseEntity<DoctorDto.response> getDoctorByUserId(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(doctorService.getDoctorByUserId(user.getId()));
    }

    @GetMapping("/filtered/v1")
    @Operation(summary = "Filter all Doctors")
    public ResponseEntity<String> getFilteredDoctorsV1(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization
            ){
        return ResponseEntity.ok(doctorService.getFilteredDoctorV1(name,specialization));
    }

    @GetMapping("/filtered/v3")
    @Operation(summary = "Filter all Doctors")
    public ResponseEntity<List<DoctorDto.response>> getFilteredDoctorsV3(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization
    ){
        return ResponseEntity.ok(doctorService.getFilteredDoctorV3(name,specialization));
    }

    @PostMapping("/schedule")
    @Operation(summary = "Add Doctor Schedule")
    public ResponseEntity<DoctorDto.scheduleResponse> addSchedule(@AuthenticationPrincipal User user, @RequestBody DoctorDto.scheduleResponse dto){
        return ResponseEntity.ok(doctorService.addDoctorSchedule(user.getId(), dto));
    }

    @GetMapping("/schedule")
    @Operation(summary = "Get doctor schedule")
    public ResponseEntity<DoctorDto.scheduleResponse> getSchedule(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(doctorService.getDoctorSchedule(user.getId()));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Doctor Profile")
    public ResponseEntity<String> getDoctorProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(doctorService.getDoctorProfile(user.getId()));
    }

    @PutMapping("/schedule")
    @Operation(summary = "Update Doctor Schedule")
    public ResponseEntity<DoctorDto.scheduleResponse> updateSchedule(@AuthenticationPrincipal User user, @RequestBody DoctorDto.scheduleResponse dto){
        return ResponseEntity.ok(doctorService.updateDoctorSchedule(user.getId(), dto));
    }
}
