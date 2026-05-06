package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.entity.Patient;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.mapper.PatientMapper;
import com.project.HospitalManagement.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientMapper patientMapper, PatientRepository patientRepository) {
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
    }

    public PatientDto.response getPatientByUserId(long userId){
        PatientDto.dbResponse patient = patientRepository.v1_findPatientByUserId(userId).orElseThrow(()->{
            log.warn("Could not get patient. Patient not found with id: {}",userId);
            return new ResourceNotFoundException("Patient not found with user id: " + userId);
        });
        return patientMapper.DbToRecord(patient);
    }

    public PatientDto.patientDetailsWithDocumentData getPatientDetailsV1(Long patientId) {
        String json = patientRepository.v1_findPatientDetails(patientId);

        if(json == null){
            throw new ResourceNotFoundException("Patient not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registeredModules();

        return objectMapper.readValue(json, PatientDto.patientDetailsWithDocumentData.class);
    }

    public PatientDto.patientDetailsWithDocumentDataAndAppointmentData getPatientDetailsV2(Long patientId, Long appointmentId){
        String json = patientRepository.v2_findPatientDetails(patientId, appointmentId);

        if(json == null){
            throw new ResourceNotFoundException("Patient not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registeredModules();

        return objectMapper.readValue(json,PatientDto.patientDetailsWithDocumentDataAndAppointmentData.class);
    }

    public PatientDto.patientDetailsWithDocumentDataAndAppointmentData getPatientDetailsV3(Long patientId, Long appointmentId){
        String json = patientRepository.v2_findPatientDetails(patientId, appointmentId);

        if(json == null){
            throw new ResourceNotFoundException("Patient not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registeredModules();

        return objectMapper.readValue(json,PatientDto.patientDetailsWithDocumentDataAndAppointmentData.class);
    }

    public String getPatientProfileDetails(Long userId){
        return patientRepository.getPatientProfile(userId);
    }

}
