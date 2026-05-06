package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.Records.SlotDto;
import com.project.HospitalManagement.entity.Doctor;
import com.project.HospitalManagement.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    boolean existsByDoctorAndDateAndStartTime(
            Doctor doctor,
            LocalDate date,
            LocalTime startTime
    );

    Optional<Slot> findByDoctorAndDateAndStartTime(
            Doctor doctor,
            LocalDate date,
            LocalTime startTime
    );

    List<Slot> findByDoctorAndDateBetween(
            Doctor doctor,
            LocalDate startDate,
            LocalDate endDate
    );

    @NativeQuery(value = """
            select s.* from slot_data s join doctor_data d on s.doctor_id = d.id where d.user_id = :userId order by slot_date, start_time
            """)
    List<Slot> findByUserId(
            @Param("userId") long userId
    );

    @NativeQuery(value = """
            select * from slot_data where id=:slotId and doctor_id = :doctorId;
            """)
    Optional<Slot> findByIdAndDoctorId(@Param("slotId")Long slotId, @Param("doctorId") Long doctorId);

    @NativeQuery(value = """
            select * from slot_data where id=:slotId and doctor_id = :doctorId and status = 'AVAILABLE' and booked_count < slot_capacity;
            """)
    Optional<Slot> findBookableSlot(@Param("slotId")Long slotId, @Param("doctorId") Long doctorId);

    @NativeQuery(value = """
            select s.* from slot_data s join doctor_data d on s.doctor_id = d.id where s.id = :slotId and d.user_id =:userId
            """)
    Optional<Slot> findByIdAndUserId(Long userId, Long slotId);


    @NativeQuery("""
            SELECT filter_slots(:doctorId, :filterDate)
            """)
    String filterSlots(@Param("doctorId") Long doctorId, @Param("filterDate") LocalDate filterDate);

    @NativeQuery("""
            select
            	s.id, s.slot_date, s.booked_count, s.slot_capacity,s.start_time, s.end_time, s.status
            from slot_data s
            where
            	s.doctor_id = :doctorId
            	and
            	slot_date = :filterDate
            order by slot_date, start_time
            """)
    List<SlotDto.dbResponse> v3_filterSlots(@Param("doctorId") Long doctorId, @Param("filterDate") LocalDate filterDate);

    @NativeQuery("""
            SELECT
            	s.id, s.slot_date, s.start_time, s.end_time, s.slot_capacity, s.booked_count, s.status, s.doctor_id,
            	(
                    SELECT a.id
                    FROM appointment a
                    JOIN patient_data p ON a.patient_id = p.id
                    WHERE a.slot_id = s.id
                    AND p.user_id =:userId
                ) AS appointment_id
            FROM slot_data s
            WHERE s.doctor_id=:doctorId AND s.slot_date=:filterDate
            ORDER BY slot_date, start_time
            """)
    List<SlotDto.dbResponseWithAppointmentIdFlag> filterSlotsByUser(@Param("userId") long userId, @Param("doctorId") Long doctorId, @Param("filterDate") LocalDate filterDate);


    @NativeQuery(value = """
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM slot_data s
            WHERE s.doctor_id = :doctorId
              AND s.slot_date = :filterDate
              AND (
                  (:newStart >= s.start_time AND :newStart < s.end_time)
                  OR
                  (:newEnd > s.start_time AND :newEnd <= s.end_time)
                  OR
                  (:newStart <= s.start_time AND :newEnd >= s.end_time)
              )
              AND (:slotId IS NULL OR s.id <> :slotId)
            """)
    boolean isSlotConflicting(
            @Param("doctorId") Long doctorId,
            @Param("filterDate") LocalDate filterDate,
            @Param("newStart") LocalTime newStart,
            @Param("newEnd") LocalTime newEnd,
            @Param("slotId") Long slotId
    );
}
