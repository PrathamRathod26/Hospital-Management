package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.AppointmentDto;
import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.Records.DocumentDto;
import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.entity.*;
import com.project.HospitalManagement.enums.*;
import com.project.HospitalManagement.exception.BadRequestException;
import com.project.HospitalManagement.exception.FileNotFoundException;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.mapper.AppointmentMapper;
import com.project.HospitalManagement.repository.*;
import com.project.HospitalManagement.util.ServiceUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AppointmentService {

    @Value("${storage.path}")
    private String storagePath;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientDocumentRepository patientDocumentRepository;
    private final AppointmentDocumentRepository appointmentDocumentRepository;
    private final ServiceUtils serviceUtils;
    private final PatientDocumentService patientDocumentService;

    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository, PatientRepository patientRepository, SlotRepository slotRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, PatientDocumentRepository patientDocumentRepository,AppointmentDocumentRepository appointmentDocumentRepository, ServiceUtils serviceUtils, PatientDocumentService patientDocumentService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientDocumentRepository = patientDocumentRepository;
        this.appointmentDocumentRepository = appointmentDocumentRepository;
        this.serviceUtils = serviceUtils;
        this.patientDocumentService = patientDocumentService;
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentV1(AppointmentDto.request dto, User user){
        Appointment appointment = appointmentMapper.RecordToEntity(dto);
        Slot slot = slotRepository.findBookableSlot(dto.slotId(), dto.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot is unavailable, full or cancelled by doctor"));

        slot.setBookedCount(slot.getBookedCount() + 1);
        if(Objects.equals(slot.getBookedCount(), slot.getSlotCapacity())){
            slot.setStatus(SlotStatus.FULL);
        }

        appointment.setStatus(Status.SCHEDULED);
        appointment.setSlot(slot);
        appointment.setPatient(user.getPatientProfile());
        appointment.setCreatedAt(LocalDateTime.now());

        return appointmentMapper.EntityToRecord(appointmentRepository.saveAndFlush(appointment));
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentV2(AppointmentDto.requestWithDocumentList dto, User user){
        if(dto.documentIds().isEmpty()){
            throw new IllegalArgumentException("No documents passed");
        }

        Appointment appointment = appointmentMapper.RecordToEntityV2(dto);
        Slot slot = slotRepository.findBookableSlot(dto.slotId(), dto.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot is unavailable, full or cancelled by doctor"));

        slot.setBookedCount(slot.getBookedCount() + 1);
        if(Objects.equals(slot.getBookedCount(), slot.getSlotCapacity())){
            slot.setStatus(SlotStatus.FULL);
        }

        appointment.setStatus(Status.SCHEDULED);
        appointment.setSlot(slot);
        appointment.setPatient(user.getPatientProfile());
        appointment.setCreatedAt(LocalDateTime.now());

        appointment = appointmentRepository.save(appointment);
        if (appointment.getAppointmentDocuments() == null){
            appointment.setAppointmentDocuments(new HashSet<>());
        }
        for(Long id : dto.documentIds()){
            PatientDocument pd = patientDocumentRepository.findByIdAndUserId(user.getId(),id).orElseThrow(()->{
                log.warn("Appointment could not be created, Document not found with id {} by user id: {}", id, user.getId());
                return new FileNotFoundException("Document not found with id: " + id);
            });

            AppointmentDocument ad = new AppointmentDocument();
            ad.setPatientDocument(pd);
            ad.setAppointment(appointment);
            ad = appointmentDocumentRepository.save(ad);
            appointment.getAppointmentDocuments().add(ad);
        }
        return appointmentMapper.EntityToRecord(appointment);
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentV3(User user, AppointmentDto.request dto, MultipartFile[] documents) {
        Slot slot = slotRepository.findBookableSlot(dto.slotId(), dto.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot is unavailable, full or cancelled by doctor"));

        Patient patient = user.getPatientProfile();
        Appointment appointment = new Appointment();
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setSlot(slot);
        appointment.setPatient(patient);
        appointment.setNotes(dto.notes());
        appointment.setReason(dto.reason());
        appointment.setStatus(Status.SCHEDULED);

        appointment = appointmentRepository.save(appointment);
        if (appointment.getAppointmentDocuments() == null){
            appointment.setAppointmentDocuments(new HashSet<>());
        }

        slot.setBookedCount(slot.getBookedCount() + 1);
        if(Objects.equals(slot.getBookedCount(), slot.getSlotCapacity())){
            slot.setStatus(SlotStatus.FULL);
        }

        if(documents != null && documents.length >0){
            log.info("Making an appointment with documents");
            for(MultipartFile document : documents){
                PatientDocument pd = patientDocumentService.UploadDocument(user.getId(), patient, document);
                AppointmentDocument ad = new AppointmentDocument();
                ad.setAppointment(appointment);
                ad.setPatientDocument(pd);
                appointmentDocumentRepository.save(ad);
            }
        } else {
            log.info("Making an appointment without documents");
        }

        return appointmentMapper.EntityToRecord(appointment);
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentDraftV1(AppointmentDto.draftRequestV1 dto, User user){
        if(dto.documentIds().isEmpty()){
            throw new IllegalArgumentException("No documents passed");
        }

        Appointment appointment = new Appointment();

        appointment.setReason(dto.reason());
        appointment.setNotes(dto.notes());
        appointment.setStatus(Status.PENDING);
        appointment.setPatient(user.getPatientProfile());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setDraft(true);

        appointment = appointmentRepository.save(appointment);
        if (appointment.getAppointmentDocuments() == null){
            appointment.setAppointmentDocuments(new HashSet<>());
        }
        for(Long id : dto.documentIds()){
            PatientDocument pd = patientDocumentRepository.findByIdAndUserId(user.getId(),id).orElseThrow(()->{
                log.warn("Draft could not be created, Document not found with id {} by user id: {}", id, user.getId());
                return new FileNotFoundException("Document not found with id: " + id);
            });

            AppointmentDocument ad = new AppointmentDocument();
            ad.setPatientDocument(pd);
            ad.setAppointment(appointment);
            ad = appointmentDocumentRepository.save(ad);
            appointment.getAppointmentDocuments().add(ad);
        }
        return appointmentMapper.EntityToRecord(appointment);
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentDraftV2(User user, AppointmentDto.draftRequestV2 dto, MultipartFile[] documents){
        if(dto.notes().isEmpty() && dto.reason().isEmpty() && documents == null){
            throw new BadRequestException("Cannot save an empty draft");
        }

        Appointment appointment = new Appointment();

        appointment.setReason(dto.reason());
        appointment.setNotes(dto.notes());
        appointment.setStatus(Status.PENDING);
        appointment.setPatient(user.getPatientProfile());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setDraft(true);

        appointment = appointmentRepository.save(appointment);
        if (appointment.getAppointmentDocuments() == null){
            appointment.setAppointmentDocuments(new HashSet<>());
        }
        if(documents != null && documents.length >0){
            log.info("Making an appointment draft with documents");
            for(MultipartFile document : documents){
                PatientDocument pd = patientDocumentService.UploadDocument(user.getId(), user.getPatientProfile(), document);
                AppointmentDocument ad = new AppointmentDocument();
                ad.setAppointment(appointment);
                ad.setPatientDocument(pd);
                appointmentDocumentRepository.save(ad);
            }
        } else {
            log.info("Making an appointment draft without documents");
        }
        return appointmentMapper.EntityToRecord(appointment);
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentForNewUserV1(AppointmentDto.newUserRequest dto) {
        Slot slot = slotRepository.findBookableSlot(dto.slotId(), dto.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot is unavailable, full or cancelled by doctor"));


        User user = userRepository.findByEmailAndRole(dto.email(), Role.PATIENT)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(dto.email());
                    newUser.setPassword(passwordEncoder.encode(dto.password()));
                    newUser.setRole(Role.PATIENT);
                    return userRepository.save(newUser);
                });

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Patient newPatient = new Patient();
                    newPatient.setUser(user);
                    newPatient.setFirstName(dto.firstName());
                    newPatient.setLastName(dto.lastName());
                    newPatient.setPhone(dto.phone());
                    newPatient.setAge(dto.age());
                    newPatient.setGender(Gender.valueOf(dto.gender()));
                    newPatient.setBloodGroup(BloodGroup.valueOf(dto.bloodGroup()));
                    newPatient.setEmergencyContact(dto.emergencyContact());
                    return patientRepository.save(newPatient);
                });

        slot.setBookedCount(slot.getBookedCount() + 1);
        if(Objects.equals(slot.getBookedCount(), slot.getSlotCapacity())){
            slot.setStatus(SlotStatus.FULL);
        }

        Appointment appointment = new Appointment();
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setSlot(slot);
        appointment.setPatient(patient);
        appointment.setNotes(dto.notes());
        appointment.setReason(dto.reason());
        appointment.setStatus(Status.SCHEDULED);

        return appointmentMapper.EntityToRecord(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentForNewUserV2(MultipartFile[] documents, AppointmentDto.newUserRequest dto) {
        Slot slot = slotRepository.findBookableSlot(dto.slotId(), dto.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot is unavailable, full or cancelled by doctor"));

        User user = serviceUtils.getOrCreateUser(dto.email(), dto.password(), Role.PATIENT);

        Patient patient = serviceUtils.getOrCreatePatient(user, dto.firstName(), dto.lastName(),dto.phone(),dto.age(),dto.gender(),dto.bloodGroup(), dto.emergencyContact());

        slot.setBookedCount(slot.getBookedCount() + 1);
        if(Objects.equals(slot.getBookedCount(), slot.getSlotCapacity())){
            slot.setStatus(SlotStatus.FULL);
        }

        Appointment appointment = new Appointment();
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setSlot(slot);
        appointment.setPatient(patient);
        appointment.setNotes(dto.notes());
        appointment.setReason(dto.reason());
        appointment.setStatus(Status.SCHEDULED);

        appointment = appointmentRepository.save(appointment);
        if (appointment.getAppointmentDocuments() == null){
            appointment.setAppointmentDocuments(new HashSet<>());
        }


        for(MultipartFile document : documents){
            PatientDocument pd = patientDocumentService.UploadDocument(user.getId(), patient, document);
            AppointmentDocument ad = new AppointmentDocument();
            ad.setAppointment(appointment);
            ad.setPatientDocument(pd);
            appointmentDocumentRepository.save(ad);
        }
        return appointmentMapper.EntityToRecord(appointment);
    }

    public List<AppointmentDto.responseWithPatientAndSlotAndDoctor> getPatientAppointmentsV1(Long userId) {
        List<AppointmentDto.dbResponse> appointments = appointmentRepository.v1_findAllPatientAppointmentsByUserId(userId);
        return appointments.stream().map(appointmentMapper::DbToResponse).toList();
    }

    public List<AppointmentDto.responseWithPatientAndSlotAndDoctor> getDoctorAppointmentsV1(Long userId) {
        List<AppointmentDto.dbResponse> appointments = appointmentRepository.v1_findAllDoctorAppointmentsByUserId(userId);
        return appointments.stream().map(appointmentMapper::DbToResponse).toList();
    }

    public String getDoctorAppointmentsV2(Long userId) {
        return appointmentRepository.v2_findAllDoctorAppointmentsByUserIdJson(userId);
    }

    public String getPatientAppointmentsV2(Long userId) {
        return appointmentRepository.v2_findAllPatientAppointmentsByUserIdJson(userId);
    }

    public PatientDto.patientDetailsWithAppointmentListWithDoctor getPatientAppointmentsV3(Long userId){
        String json = appointmentRepository.v3_getPatientAppointments(userId);

        if(json==null){
            throw new BadCredentialsException("");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registeredModules();

        return objectMapper.readValue(json, PatientDto.patientDetailsWithAppointmentListWithDoctor.class);
    }

    public DoctorDto.doctorDetailsAndSlotListWithAppointmentListWithPatient getDoctorAppointmentsV3(Long userId){
        String json = appointmentRepository.v3_getDoctorAppointments(userId);

        if(json==null){
            throw new BadCredentialsException("");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registeredModules();

        return objectMapper.readValue(json, DoctorDto.doctorDetailsAndSlotListWithAppointmentListWithPatient.class);
    }

    public AppointmentDto.responseWithPatientAndSlotAndDoctor getPatientAppointment(Long userId, Long appointmentId){
        Appointment appointment = appointmentRepository.findByIdAndPatientUserId(appointmentId,userId).orElseThrow(()->{
            log.warn("Appointment not found with id: {}", appointmentId);
            return new ResourceNotFoundException("Appointment not found with id: " + appointmentId);
        });

        return appointmentMapper.EntityToRecord(appointment);
    }

    @Transactional
    public void DeleteDocumentFromAppointment (User user, Long appointmentId, Long patientDocumentId){
        int response = appointmentDocumentRepository.removeDocumentFromDraft(appointmentId,user.getPatientProfile().getId(), patientDocumentId);
        if(response==0){
            throw new ResourceNotFoundException("Document not found");
        }
    }

    @Transactional
    public AppointmentDto.responseWithPatientAndSlotAndDoctor makeAppointmentFromDraft(User user, Long appointmentId, AppointmentDto.request dto, MultipartFile[] documents){
        Appointment appointment = appointmentRepository.findByIdAndPatientUserId(appointmentId,user.getId()).orElseThrow(()->{
            log.warn("Appointment not found with id: {} and user id: {}", appointmentId, user.getId());
            return new ResourceNotFoundException("Draft not found with id: " + appointmentId);
        });

        Slot slot = slotRepository.findBookableSlot(dto.slotId(), dto.doctorId()).orElseThrow(()->{
            log.warn("Bookable slot cound not be found with id: {} and doctor id: {}", dto.slotId(), dto.doctorId());
            return new ResourceNotFoundException("Bookable slot not found");
        });

        appointment.setNotes(dto.notes());
        appointment.setDraft(false);
        appointment.setSlot(slot);
        appointment.setStatus(Status.SCHEDULED);

        slot.setBookedCount(slot.getBookedCount() + 1);
        if(Objects.equals(slot.getBookedCount(), slot.getSlotCapacity())){
            slot.setStatus(SlotStatus.FULL);
        }

        if(documents!=null){
            for(MultipartFile document: documents){
                PatientDocument pd = patientDocumentService.UploadDocument(user.getId(), user.getPatientProfile(), document);
                AppointmentDocument ad = new AppointmentDocument();
                ad.setAppointment(appointment);
                ad.setPatientDocument(pd);
                appointmentDocumentRepository.save(ad);
            }
        }

        return appointmentMapper.EntityToRecord(appointmentRepository.save(appointment));
    }

    public List<DocumentDto.response> getAttachedDocumentAsAuthenticated(Long appointmentId){
        return patientDocumentRepository.getAttachedDocumentAsAuthenticated(appointmentId);
    }

    public AppointmentDto.responseWithPatientAndSlotAndDoctor updateDraftAppointment(User user, Long appointmentId, AppointmentDto.draftRequestV2 dto, MultipartFile[] documents) {
        Appointment appointment = appointmentRepository.findByIdAndPatientUserId(appointmentId,user.getId()).orElseThrow(()->{
            log.warn("Appointment not found with id: {} and user id: {}", appointmentId, user.getId());
            return new ResourceNotFoundException("Draft not found with id: " + appointmentId);
        });

        appointment.setNotes(dto.notes());
        appointment.setReason(dto.reason());

        if(documents!=null){
            for(MultipartFile document: documents){
                PatientDocument pd = patientDocumentService.UploadDocument(user.getId(), user.getPatientProfile(), document);
                AppointmentDocument ad = new AppointmentDocument();
                ad.setAppointment(appointment);
                ad.setPatientDocument(pd);
                appointmentDocumentRepository.save(ad);
            }
        }

        return appointmentMapper.EntityToRecord(appointmentRepository.save(appointment));
    }
}
