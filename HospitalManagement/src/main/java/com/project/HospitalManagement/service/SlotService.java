package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.SlotDto;
import com.project.HospitalManagement.entity.Doctor;
import com.project.HospitalManagement.entity.DoctorSchedule;
import com.project.HospitalManagement.entity.Slot;
import com.project.HospitalManagement.enums.SlotStatus;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.mapper.SlotMapper;
import com.project.HospitalManagement.repository.DoctorRepository;
import com.project.HospitalManagement.repository.DoctorScheduleRepository;
import com.project.HospitalManagement.repository.SlotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class SlotService {
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;
    private final DoctorRepository doctorRepository;

    public SlotService(DoctorScheduleRepository doctorScheduleRepository, SlotRepository slotRepository, SlotMapper slotMapper, DoctorRepository doctorRepository) {
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.slotRepository = slotRepository;
        this.slotMapper = slotMapper;
        this.doctorRepository = doctorRepository;
    }

    public void generateSlotForAllDoctors(){
        List<DoctorSchedule> schedules = doctorScheduleRepository.findAll();

        for(DoctorSchedule schedule: schedules){
            try{
                log.info("Generating Slot for doctor id: {}", schedule.getDoctor().getId());
                generateSlotForDoctor(schedule);
            } catch (Exception e){
                log.error("Error generating Slot for doctor id: {}", schedule.getDoctor().getId());
            }
        }
    }

    public void generateSlotForDoctor(DoctorSchedule schedule){
        Doctor doctor = schedule.getDoctor();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);

        int slotsCreated = 0;

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            if (schedule.getDaysOfWeek().contains(dayOfWeek)) {
                slotsCreated += generateSlotsForDay(doctor, schedule, date);
            }
        }
            log.info("{} slots generated for doctor id: {}", slotsCreated, doctor.getId());
    }

    private int generateSlotsForDay(Doctor doctor, DoctorSchedule schedule, LocalDate date) {
        LocalTime currentTime = schedule.getWorkStartTime();
        LocalTime endTime = schedule.getWorkEndTime();
        int slotDuration = schedule.getSlotDuration();
        int slotsCreated = 0;

        while (currentTime.plusMinutes(slotDuration).isBefore(endTime) ||
                currentTime.plusMinutes(slotDuration).equals(endTime)) {

            LocalTime slotEndTime = currentTime.plusMinutes(slotDuration);

            boolean exists = slotRepository.existsByDoctorAndDateAndStartTime(
                    doctor, date, currentTime
            );

            if (!exists) {
                Slot slot = Slot.builder()
                        .doctor(doctor)
                        .date(date)
                        .startTime(currentTime)
                        .endTime(slotEndTime)
                        .slotCapacity(schedule.getDefaultSlotCapacity())
                        .bookedCount(0)
                        .status(SlotStatus.AVAILABLE)
                        .build();

                slotRepository.save(slot);
                slotsCreated++;
            }

            currentTime = slotEndTime;
        }
        return slotsCreated;
    }

    public String getSlots(Long doctorId, LocalDate date){
        return slotRepository.filterSlots(doctorId,date);
    }

    public List<SlotDto.response> getSlotsV3(Long doctorId, LocalDate date){
        List<SlotDto.dbResponse> list =  slotRepository.v3_filterSlots(doctorId,date);
        return list.stream().map(slotMapper::DbToRecord).toList();
    }

    public List<SlotDto.responseWithAppointmentIdFlag> getSlotsByUser(Long userId, Long doctorId, LocalDate date){
        List<SlotDto.dbResponseWithAppointmentIdFlag> list = slotRepository.filterSlotsByUser(userId,doctorId,date);
        return list.stream().map(slotMapper::DbAppointmentFlagToRecord).toList();
    }

    public List<SlotDto.response> getAllDoctorSlotsByUserId(Long userId){
        List<Slot> list = slotRepository.findByUserId(userId);
        return list.stream().map(slotMapper::EntityToRecord).toList();
    }

    public void disableSlot(Long userId, Long slotId){
        Slot slot = slotRepository.findByIdAndUserId(userId,slotId).orElseThrow(()->{
            log.warn("Could not disable slot. Slot not found with slot id: {} and user id: {}", slotId, userId);
            return new ResourceNotFoundException("Slot not found with slot id: " + slotId + " and user id: " + userId);
        });
        slot.setStatus(SlotStatus.DISABLED);
    }

    public SlotDto.response updateSlot(Long userId, SlotDto.request dto) {
        Slot slot = slotRepository.findByIdAndUserId(userId, dto.id()).orElseThrow(() -> {
            log.warn("Could not update slot. Slot not found with slot id: {} and user id: {}", dto.id(), userId);
            return new ResourceNotFoundException("Slot not found with slot id: " + dto.id() + " and user id: " + userId);
        });

        LocalTime startTimeToValidate = (dto.startTime() != null) ? dto.startTime() : slot.getStartTime();
        LocalTime endTimeToValidate = (dto.endTime() != null) ? dto.endTime() : slot.getEndTime();

        if (slotRepository.isSlotConflicting(slot.getDoctor().getId(), slot.getDate(), startTimeToValidate, endTimeToValidate, dto.id())) {
            throw new IllegalStateException("The updated time range conflicts with an existing slot for this doctor.");
        }

        if (dto.startTime() != null) {
            slot.setStartTime(dto.startTime());
        }

        if (dto.endTime() != null) {
            slot.setEndTime(dto.endTime());
        }

        if (dto.slotCapacity() != null) {
            if (dto.slotCapacity() >= slot.getBookedCount()) {
                slot.setSlotCapacity(dto.slotCapacity());
            } else {
                throw new IllegalStateException("Slot capacity (" + dto.slotCapacity() + ") cannot be less than the number of existing bookings (" + slot.getBookedCount() + ").");
            }
        }

        if (dto.status() != null) {
            slot.setStatus(dto.status());
        }

        return slotMapper.EntityToRecord(slotRepository.save(slot));
    }

    public SlotDto.response makeSlot(Long userId, SlotDto.request dto){
        Doctor doctor = doctorRepository.findByUserId(userId).orElseThrow(()->{
            log.warn("Could not make slot, Doctor not found with user id: {}", userId);
            return new ResourceNotFoundException("Doctor not found with user id: " + userId);
        });
        if(slotRepository.isSlotConflicting(doctor.getId(), dto.date(), dto.startTime(), dto.endTime(), null)){
            throw new IllegalStateException("The updated time range conflicts with an existing slot for this doctor.");
        }

        Slot slot = slotMapper.RecordToEntity(dto);
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setId(null);
        slot.setBookedCount(0);
        slot.setDoctor(doctor);

        return slotMapper.EntityToRecord(slotRepository.save(slot));
    }

}
