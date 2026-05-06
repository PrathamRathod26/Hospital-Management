package com.project.HospitalManagement.Records;

public class PrescriptionDto {
    public record request(
            String diagnosis,
            String medicationNotes,
            String advice
    ){}

    public record base(
            Long id,
            String diagnosis,
            String medicationNotes,
            String advice
    ){}

    public record response(
            Long id,
            AppointmentDto.responseWithPatientAndSlotAndDoctor appointment,
            String diagnosis,
            String medicationNotes,
            String advice
    ){}
}
