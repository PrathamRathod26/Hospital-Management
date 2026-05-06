package com.project.HospitalManagement.Records;

import com.project.HospitalManagement.enums.SlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class SlotDto {
    public record request(
            Long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer slotCapacity,
            SlotStatus status
    ){}

    public record response(
            Long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Integer bookedCount,
            SlotStatus status
    ){}

    public record dbResponse(
            Long id,
            LocalDate date,
            Integer bookedCount,
            Integer capacity,
            LocalTime startTime,
            LocalTime endTime,
            String status
    ){}

    public record dbResponseWithAppointmentIdFlag(
            Long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Integer bookedCount,
            String status,
            Long doctorId,
            Long appointmentId
    ){}

    public record responseWithAppointmentIdFlag(
            Long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Integer bookedCount,
            SlotStatus status,
            Long doctorId,
            Long appointmentId
    ){}
}
