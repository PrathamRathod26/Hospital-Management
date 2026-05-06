package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.PrescriptionDto;
import com.project.HospitalManagement.entity.Appointment;
import com.project.HospitalManagement.entity.Prescription;
import com.project.HospitalManagement.enums.Status;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.mapper.AppointmentMapper;
import com.project.HospitalManagement.repository.AppointmentRepository;
import com.project.HospitalManagement.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(AppointmentMapper appointmentMapper, PrescriptionRepository prescriptionRepository, AppointmentRepository appointmentRepository) {
        this.appointmentMapper = appointmentMapper;
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public PrescriptionDto.response addPrescription(Long userId, Long appointmentId, PrescriptionDto.request dto) {
        Appointment appointment = appointmentRepository.findByIdAndDoctorUserId(appointmentId,userId).orElseThrow(()->{
            log.warn("Could not add prescription, Appointment not found with id: {}", appointmentId);
            return new ResourceNotFoundException("Appointment not found with id: " + appointmentId);
        });

        appointment.setStatus(Status.COMPLETED);
        appointment = appointmentRepository.save(appointment);

        Prescription prescription = appointmentMapper.PrescriptionRecordToEntity(dto);
        prescription.setAppointment(appointment);
        return appointmentMapper.PrescriptionEntityToRecord(prescriptionRepository.save(prescription));
    }

    public PrescriptionDto.response getPrescriptionAsPatient(Long userId, Long appointmentId){
        return appointmentMapper.PrescriptionEntityToRecord(prescriptionRepository.findByAppointmentIdAndUserId(userId,appointmentId).orElseThrow(()->{
            log.warn("Prescription not found by appointment Id: {}", appointmentId);
            return new ResourceNotFoundException("Prescription not found by appointment Id: " + appointmentId);
        }));
    }
}
