package com.project.HospitalManagement.Records;

import com.project.HospitalManagement.enums.BloodGroup;
import com.project.HospitalManagement.enums.Gender;
import com.project.HospitalManagement.enums.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PatientDto {
    public record registerRequest(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            Integer age,
            String gender,
            String bloodGroup,
            String emergencyContact
    ){}

    public record dbResponse(
            Long id,
            String firstName,
            String lastName,
            String phone,
            Integer age,
            String gender,
            String bloodGroup,
            String emergencyContact
    ){}

    public record response(
            Long id,
            String firstName,
            String lastName,
            String phone,
            Integer age,
            Gender gender,
            BloodGroup bloodGroup,
            String emergencyContact
    ){}
//    {patient,appointment[... ,doctor]}
    public record patientDetailsWithAppointmentListWithDoctor(
            PatientDto.response patient,
            List<patientAppointmentWithDoctor> appointments
    ){}

//    helper
//    {..., doctor}
    private record patientAppointmentWithDoctor(
            Long id,
            String notes,
            String reason,
            Status status,
            LocalDate date,
            LocalTime startTime,
            DoctorDto.response doctor,
            PrescriptionDto.base prescription
    ){}

    public record patientDetailsWithDocumentData(
            PatientDto.response patient,
            List<DocumentDto.response> documents
    ){}

//    {patient,document[],appointment{..., prescription}}
    public record patientDetailsWithDocumentDataAndAppointmentData(
            PatientDto.response patient,
            List<DocumentDto.responseWithUrl> documents,
            PatientAppointment appointment

    ){}

//    helper
//    {..., prescription}
    private record PatientAppointment(
            Long id,
            String notes,
            String reason,
            Status status,
            LocalDate date,
            LocalTime startTime,
            PrescriptionDto.base prescription
    ){}
}
