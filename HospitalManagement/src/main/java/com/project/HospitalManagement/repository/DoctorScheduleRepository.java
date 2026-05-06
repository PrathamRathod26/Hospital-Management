package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    @NativeQuery("""
            SELECT s.id, s.work_days, s.work_start_time, s.work_end_time, s.slot_duration, s.default_slot_capacity FROM doctor_schedule s
            JOIN doctor_data d on d.id=s.doctor_id
            WHERE d.user_id = :userId
            """)
    Optional<DoctorDto.dbScheduleResponse> findByUserId(@Param("userId") long userId);

}
