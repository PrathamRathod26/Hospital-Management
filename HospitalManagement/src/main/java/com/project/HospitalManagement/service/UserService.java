package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.Records.UserDto;
import com.project.HospitalManagement.config.AuthUtil;
import com.project.HospitalManagement.entity.Doctor;
import com.project.HospitalManagement.entity.Patient;
import com.project.HospitalManagement.entity.PatientDocument;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.enums.Role;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.mapper.DoctorMapper;
import com.project.HospitalManagement.mapper.PatientMapper;
import com.project.HospitalManagement.repository.PatientDocumentRepository;
import com.project.HospitalManagement.repository.PatientRepository;
import com.project.HospitalManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Value("${storage.path}")
    private String storagePath;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final PasswordEncoder passwordEncoder;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final PatientDocumentRepository patientDocumentRepository;
    private final PatientRepository patientRepository;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, AuthUtil authUtil, PasswordEncoder passwordEncoder, PatientMapper patientMapper, DoctorMapper doctorMapper, PatientDocumentRepository patientDocumentRepository, PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.authUtil = authUtil;
        this.passwordEncoder = passwordEncoder;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
        this.patientDocumentRepository = patientDocumentRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public PatientDto.response registerPatientV1(PatientDto.registerRequest dto) {
        if (userRepository.existsByEmailAndRole(dto.email(), Role.PATIENT)) {
            throw new IllegalStateException("A patient profile already exists for this email.");
        }

        User user = userRepository.findByEmailAndRole(dto.email(), Role.PATIENT)
                .orElseGet(() -> {
                    log.info("Creating new User persona for email: {} with role: PATIENT", dto.email());
                    User newUser = new User();
                    newUser.setEmail(dto.email());
                    newUser.setPassword(passwordEncoder.encode(dto.password()));
                    newUser.setRole(Role.PATIENT);
                    return userRepository.save(newUser);
                });

        Patient patient = patientMapper.RecordToEntity(dto);

        patient.setUser(user);
        user.setPatientProfile(patient);

        userRepository.save(user);

        log.info("Success: Patient profile linked to user id: {}", user.getId());
        return patientMapper.EntityToRecord(patient);
    }

    @Transactional
    public PatientDto.response registerPatientV2(PatientDto.registerRequest dto, MultipartFile document) {
        if (userRepository.existsByEmailAndRole(dto.email(), Role.PATIENT)) {
            throw new IllegalStateException("A patient profile already exists for this email.");
        }

        User user = userRepository.findByEmailAndRole(dto.email(), Role.PATIENT)
                .orElseGet(() -> {
                    log.info("V2: Creating new User persona for email: {} with role: PATIENT", dto.email());
                    User newUser = new User();
                    newUser.setEmail(dto.email());
                    newUser.setPassword(passwordEncoder.encode(dto.password()));
                    newUser.setRole(Role.PATIENT);
                    return userRepository.save(newUser);
                });

        Patient patient = patientMapper.RecordToEntity(dto);
        patient.setUser(user);

        patient = patientRepository.saveAndFlush(patient);
        user.setPatientProfile(patient);

        Path userDirectory = Paths.get(storagePath, user.getId().toString());

        try {
            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory);
            }

            String originalFileName = document.getOriginalFilename();
            String extension = (originalFileName != null && originalFileName.contains("."))
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";

            String storedName = UUID.randomUUID().toString() + extension;
            Path targetLocation = userDirectory.resolve(storedName);

            Files.copy(document.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            PatientDocument doc = new PatientDocument();
            doc.setPatient(patient);
            doc.setFileName(originalFileName);
            doc.setStoredName(storedName);
            doc.setFilePath(targetLocation.toString());

            patientDocumentRepository.save(doc);

        } catch (IOException e) {
            log.error("File system error for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Could not store the file. Registration rolled back.");
        }

        log.info("V2: Success: Patient profile linked to user id: {}", user.getId());
        return patientMapper.EntityToRecord(patient);
    }

    @Transactional
    public DoctorDto.response registerDoctor(DoctorDto.registerRequest dto){
        if (userRepository.existsByEmailAndRole(dto.email(), Role.DOCTOR)) {
            throw new IllegalStateException("A doctor profile already exists for this email.");
        }

        User user =  userRepository.findByEmailAndRole(dto.email(), Role.DOCTOR)
                .orElseGet(()->{
                    log.warn("Creating new User persona for email: {} with role: Doctor", dto.email());
                    User newUser = new User();
                    newUser.setEmail(dto.email());
                    newUser.setPassword(passwordEncoder.encode(dto.password()));
                    newUser.setRole(Role.DOCTOR);
                    return userRepository.save(newUser);
                });

        Doctor doctor = doctorMapper.RecordToEntity(dto);

        doctor.setUser(user);
        user.setDoctorProfile(doctor);

        user = userRepository.save(user);

        log.info("Success: Doctor profile linked to user id: {}", user.getId());
        return doctorMapper.EntityToRecord(user.getDoctorProfile());
    }

    @Transactional
    public UserDto.response registerStaff(UserDto.registerRequest dto){
        if (userRepository.existsByEmailAndRole(dto.email(), Role.STAFF)) {
            throw new IllegalStateException("A staff profile already exists for this email.");
        }

        User newUser = new User();
        newUser.setEmail(dto.email());
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setRole(Role.STAFF);
        newUser = userRepository.save(newUser);
        return new UserDto.response(newUser.getId(), newUser.getEmail());
    }

    public UserDto.auth patientLogin(UserDto.loginRequest dto){

        String compositeUsername = dto.email() + "|" + Role.PATIENT.name();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(compositeUsername, dto.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = authUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return new UserDto.auth(user.getId(), accessToken, refreshToken);
    }

    public UserDto.auth doctorLogin(UserDto.loginRequest dto){
        String compositeUsername = dto.email() + "|" + Role.DOCTOR.name();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(compositeUsername, dto.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = authUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return new UserDto.auth(user.getId(), accessToken, refreshToken);
    }

    public UserDto.auth staffLogin(UserDto.loginRequest dto){
        String compositeUsername = dto.email() + "|" + Role.STAFF.name();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(compositeUsername, dto.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = authUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return new UserDto.auth(user.getId(), accessToken, refreshToken);
    }

    public UserDto.auth refresh(UserDto.refreshRequest requestDto){
        authUtil.validateRefreshToken(requestDto.refreshToken());

        User user = userRepository.findByRefreshToken(requestDto.refreshToken()).orElseThrow(()->{
            log.warn("Invalid refresh token");
            return new ResourceNotFoundException("Invalid refresh token");
        });
        return new UserDto.auth(user.getId(), authUtil.generateAccessToken(user), "Access token updated");
    }

    public Optional<User> getPatientEverythingByUserId(long userId){
        return userRepository.findPatientEverything(userId);
    }

    public Optional<User> getDoctorEverythingByUserId(long userId){
        return userRepository.findDoctorEverything(userId);
    }

    public String getAllUser() {
        return userRepository.getAllUser();
    }
}
