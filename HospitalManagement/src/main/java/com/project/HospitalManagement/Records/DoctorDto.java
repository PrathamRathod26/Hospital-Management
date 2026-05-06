package com.project.HospitalManagement.Records;

import com.project.HospitalManagement.enums.SlotStatus;
import com.project.HospitalManagement.enums.Status;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class DoctorDto {
    public record registerRequest(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            String specialization,
            String licenseNumber
    ){}

    public record response(
            Long id,
            String firstName,
            String lastName,
            String phone,
            String specialization,
            String licenseNumber
    ){}

    public record dbResponse(
            Long id,
            String firstName,
            String lastName,
            String phone,
            String specialization,
            String licenseNumber
    ){}

    public record dbScheduleResponse(
            Long id,
            String[] daysOfWeek,
            LocalTime workStartTime,
            LocalTime workEndTime,
            Integer slotDuration,
            Integer defaultSlotCapacity
    ){}

    public record scheduleResponse(
            Long id,
            Set<DayOfWeek> daysOfWeek,
            LocalTime workStartTime,
            LocalTime workEndTime,
            Integer slotDuration,
            Integer defaultSlotCapacity
    ){}


    //    {doctor,slots[... ,appointments[... ,patient]]}
    public record doctorDetailsAndSlotListWithAppointmentListWithPatient(
            DoctorDto.response doctor,
            List<doctorSlotWithAppointments> slots
    ){}

    //    {... ,appointments[... ,patient]}
    //    helper
    private record doctorSlotWithAppointments(
            Long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Integer bookedCount,
            SlotStatus status,
            List<doctorAppointment> appointments
    ){}

    //    {... ,patient}
    //    helper
    private record doctorAppointment(
            Long id,
            Status status,
            String reason,
            String notes,
            PatientDto.response patient
    ){}
}
