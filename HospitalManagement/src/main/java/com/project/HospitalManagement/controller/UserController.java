package com.project.HospitalManagement.controller;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.Records.UserDto;
import com.project.HospitalManagement.config.AuthUtil;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.exception.FileMandatoryException;
import com.project.HospitalManagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/user")
@Tag(name="User APIs")
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthUtil authUtil;

    public UserController(UserService userService, AuthUtil authUtil) {
        this.userService = userService;
        this.authUtil = authUtil;
    }

    @PostMapping("/patient/register/v1")
    @Operation(summary = "Register new user")
    public ResponseEntity<PatientDto.response> patientRegisterV1(
            @RequestBody PatientDto.registerRequest dto
            ){
        return ResponseEntity.ok(userService.registerPatientV1(dto));
    }

    @PostMapping(value = "/patient/register/v2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register new user with mandatory document")
    public ResponseEntity<PatientDto.response> patientRegisterV2(
            @RequestPart("data") PatientDto.registerRequest dto,
            @RequestPart("document") MultipartFile document
    ){
        if (document.isEmpty()) {
            throw new FileMandatoryException("A medical document is required for registration.");
        }

        PatientDto.response response = userService.registerPatientV2(dto, document);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/patient/")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/doctor/register")
    @Operation(summary = "Register new Doctor")
    public ResponseEntity<DoctorDto.response> doctorRegister(@RequestBody DoctorDto.registerRequest dto){

        DoctorDto.response response = userService.registerDoctor(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/doctor/")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/staff/register")
    @Operation(summary = "Register new Staff")
    public ResponseEntity<UserDto.response> staffRegister(@RequestBody UserDto.registerRequest dto){
        UserDto.response response = userService.registerStaff(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/doctor/")
                .build()
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/patient/login")
    @Operation(summary = "Login Patient")
    public ResponseEntity<UserDto.auth> patientLogin(@RequestBody UserDto.loginRequest dto, HttpServletResponse response){
        UserDto.auth res = userService.patientLogin(dto);
        ResponseCookie cookie = authUtil.generateCookie(res.RefreshToken());

        UserDto.auth newResponse = new UserDto.auth(res.id(), res.accessToken(),"Refresh token set as cookies");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(newResponse);
    }

    @PostMapping("/doctor/login")
    @Operation(summary = "Login Doctor")
    public ResponseEntity<UserDto.auth> doctorLogin(@RequestBody UserDto.loginRequest dto, HttpServletResponse response){
        UserDto.auth res = userService.doctorLogin(dto);
        ResponseCookie cookie = authUtil.generateCookie(res.RefreshToken());

        UserDto.auth newResponse = new UserDto.auth(res.id(), res.accessToken(), "Refresh token set as cookies");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(newResponse);
    }

    @PostMapping("/staff/login")
    @Operation(summary = "Login Staff")
    public ResponseEntity<UserDto.auth> staffLogin(@RequestBody UserDto.loginRequest dto, HttpServletResponse response){
        UserDto.auth res = userService.staffLogin(dto);
        ResponseCookie cookie = authUtil.generateCookie(res.RefreshToken());

        UserDto.auth newResponse = new UserDto.auth(res.id(), res.accessToken(), "Refresh token set as cookies");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(newResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh user token")
    public ResponseEntity<UserDto.auth> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken){
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDto.refreshRequest dto = new UserDto.refreshRequest(refreshToken);
        return ResponseEntity.ok(userService.refresh(dto));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users")
    public ResponseEntity<String> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/patient/everything")
    @Operation(summary = "Get everything by user id")
    public ResponseEntity<?> getPatientEverythingByUserId(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.getPatientEverythingByUserId(user.getId()));
    }

    @GetMapping("/doctor/everything")
    @Operation(summary = "Get everything by user id")
    public ResponseEntity<?> getDoctorEverythingByUserId(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.getDoctorEverythingByUserId(user.getId()));
    }
}
