package com.project.HospitalManagement.service;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.entity.Doctor;
import com.project.HospitalManagement.entity.DoctorSchedule;
import com.project.HospitalManagement.exception.DuplicateResourceException;
import com.project.HospitalManagement.exception.ResourceNotFoundException;
import com.project.HospitalManagement.mapper.DoctorMapper;
import com.project.HospitalManagement.repository.DoctorRepository;
import com.project.HospitalManagement.repository.DoctorScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorMapper doctorMapper, DoctorRepository doctorRepository, DoctorScheduleRepository doctorScheduleRepository) {
        this.doctorMapper = doctorMapper;
        this.doctorRepository = doctorRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    public DoctorDto.response getDoctorByUserId(long userId){
        DoctorDto.dbResponse doctor = doctorRepository.v1_findDoctorByUserId(userId).orElseThrow(()->{
            log.warn("Could not get doctor. Doctor not found with user id: {}", userId);
            return new ResourceNotFoundException("Doctor not found with user id: " + userId);
        });
        return doctorMapper.DbToRecord(doctor);
    }

    public String getFilteredDoctorV1(String name, String specialization) {
        return doctorRepository.v1_getFilteredDoctor(name, specialization);
    }

    public List<DoctorDto.response> getFilteredDoctorV3(String name, String specialization) {
        return doctorRepository.v3_getFilteredDoctor(name, specialization);
    }

    public DoctorDto.scheduleResponse addDoctorSchedule(long userId, DoctorDto.scheduleResponse dto) {

        Doctor doctor = doctorRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Doctor not found with user id: {}", userId);
            return new ResourceNotFoundException("Doctor not found with user id: " + userId);
        });

        if(doctor.getSchedule() != null){
            throw new DuplicateResourceException("Doctor already has a schedule");
        }

        DoctorSchedule schedule = doctorMapper.ScheduleRecordToEntity(dto);

        doctor.setSchedule(schedule);
        schedule.setDoctor(doctor);

        doctor = doctorRepository.save(doctor);

        return doctorMapper.ScheduleEntityToRecord(doctor.getSchedule());
    }

    public DoctorDto.scheduleResponse getDoctorSchedule(long userId){
        DoctorDto.dbScheduleResponse dto = doctorScheduleRepository.findByUserId(userId).orElseThrow(()->{
            log.warn("Schedule not found with userId: {}", userId);
            return new ResourceNotFoundException("Schedule not found for User: " + userId);
        });

        return doctorMapper.ScheduleDbToRecord(dto);
    }

    public DoctorDto.scheduleResponse updateDoctorSchedule(long userId, DoctorDto.scheduleResponse dto) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with user id: " + userId));

        DoctorSchedule schedule = doctor.getSchedule();

        if (schedule == null) {
            schedule = new DoctorSchedule();
            schedule.setDoctor(doctor);
            doctor.setSchedule(schedule);
        }

        if (dto.daysOfWeek() != null && !dto.daysOfWeek().isEmpty()) {
            schedule.setDaysOfWeek(dto.daysOfWeek());
        }
        if (dto.workStartTime() != null) {
            schedule.setWorkStartTime(dto.workStartTime());
        }
        if (dto.workEndTime() != null) {
            schedule.setWorkEndTime(dto.workEndTime());
        }
        if (dto.defaultSlotCapacity() != null) {
            schedule.setDefaultSlotCapacity(dto.defaultSlotCapacity());
        }
        if(dto.slotDuration() != null){
            schedule.setSlotDuration(dto.slotDuration());
        }
        doctorScheduleRepository.save(schedule);
        return doctorMapper.ScheduleEntityToRecord(schedule);
    }

    public String getDoctorProfile(Long userId) {
        return doctorRepository.getDoctorProfile(userId);
    }
}
