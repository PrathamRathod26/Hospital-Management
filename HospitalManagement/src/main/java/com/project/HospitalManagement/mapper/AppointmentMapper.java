package com.project.HospitalManagement.mapper;

import com.project.HospitalManagement.Records.*;
import com.project.HospitalManagement.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "id", source = "patientId")
    @Mapping(target = "firstName", source = "patientFirstName")
    @Mapping(target = "lastName", source = "patientLastName")
    @Mapping(target = "phone", source = "patientPhone")
    @Mapping(target = "age", source = "patientAge")
    @Mapping(target = "gender", source = "patientGender")
    @Mapping(target = "bloodGroup", source = "patientBloodGroup")
    @Mapping(target = "emergencyContact", source = "patientEmergencyContact")
    PatientDto.response DbAppointmentToPatient(AppointmentDto.dbResponse appointment);

    @Mapping(target = "id", source = "slotId")
    @Mapping(target = "date", source = "slotDate")
    @Mapping(target = "startTime", source = "slotStartTime")
    @Mapping(target = "endTime", source = "slotEndTime")
    @Mapping(target = "capacity", source = "slotCapacity")
    @Mapping(target = "bookedCount", source = "slotBookedCount")
    @Mapping(target = "status", source = "slotStatus")
    SlotDto.response DbAppointmentToSlot(AppointmentDto.dbResponse appointment);

    @Mapping(target = "id", source = "doctorId")
    @Mapping(target = "firstName", source = "doctorFirstName")
    @Mapping(target = "lastName", source = "doctorLastName")
    @Mapping(target = "phone", source = "doctorPhone")
    @Mapping(target = "specialization", source = "doctorSpecialization")
    @Mapping(target = "licenseNumber", source = "doctorLicenseNumber")
    DoctorDto.response DbAppointmentToDoctor(AppointmentDto.dbResponse appointment);


    Appointment RecordToEntity(AppointmentDto.request dto);

    Appointment RecordToEntityV2(AppointmentDto.requestWithDocumentList dto);

    @Mapping(target = "doctor", source = "appointment.slot.doctor")
    @Mapping(target = "slot.capacity", source = "appointment.slot.slotCapacity")
    AppointmentDto.responseWithPatientAndSlotAndDoctor EntityToRecord(Appointment appointment);

    @Mapping(target = "id", source = "appointmentId")
    @Mapping(target = "patient", source = "appointment")
    @Mapping(target = "slot", source = "appointment")
    @Mapping(target = "doctor", source = "appointment")
    @Mapping(target = "status", source = "appointmentStatus")
    @Mapping(target = "reason", source = "appointmentReason")
    @Mapping(target = "notes", source = "appointmentNotes")
    AppointmentDto.responseWithPatientAndSlotAndDoctor DbToResponse (AppointmentDto.dbResponse appointment);

    Prescription PrescriptionRecordToEntity(PrescriptionDto.request dto);
    PrescriptionDto.response PrescriptionEntityToRecord(Prescription prescription);
}
