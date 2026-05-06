package com.project.HospitalManagement.Records;

import com.project.HospitalManagement.enums.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentDto {
    public record request(
            Long doctorId,
            Long slotId,
            String reason,
            String notes
    ){}

    public record requestWithDocumentList(
            Long doctorId,
            Long slotId,
            String reason,
            String notes,
            List<Long> documentIds
    ){}

    public record draftRequestV1(
            String reason,
            String notes,
            List<Long> documentIds
    ){}

    public record draftRequestV2(
            String reason,
            String notes
    ){}

    public record newUserRequest(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            Integer age,
            String gender,
            String bloodGroup,
            String emergencyContact,
            Long doctorId,
            Long slotId,
            String reason,
            String notes
    ){}

    // {..., patient,slot{... ,doctor}}
    public record responseWithPatientAndSlotAndDoctor(
            Long id,
            PatientDto.response patient,
            SlotDto.response slot,
            DoctorDto.response doctor,
            Status status,
            String reason,
            String notes
    ){}

    public record dbResponse(
            Long patientId,
            String patientFirstName,
            String patientLastName,
            String patientPhone,
            Integer patientAge,
            String patientGender,
            String patientBloodGroup,
            String patientEmergencyContact,

            Long doctorId,
            String doctorFirstName,
            String doctorLastName,
            String doctorPhone,
            String doctorSpecialization,
            String doctorLicenseNumber,

            Long slotId,
            LocalDate slotDate,
            LocalTime slotStartTime,
            LocalTime slotEndTime,
            Integer slotCapacity,
            Integer slotBookedCount,
            String slotStatus,

            Long appointmentId,
            String appointmentStatus,
            String appointmentReason,
            String appointmentNotes
    ){}

    public record draftResponse(
            Long id,
            Status status,
            String reason,
            String notes,
            Boolean draft
    ){}

}
