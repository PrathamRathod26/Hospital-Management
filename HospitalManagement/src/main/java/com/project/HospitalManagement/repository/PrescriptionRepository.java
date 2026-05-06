package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {
    Optional<Prescription> findByAppointmentId(Long appointmentId);

    @NativeQuery("""
            select
            	pre.*
            from prescription pre
            join appointment a on a.id = pre.appointment_id
            join patient_data p on p.id = a.patient_id
            where p.user_id = :userId and a.id=:appointmentId
            """)
    Optional<Prescription> findByAppointmentIdAndUserId(@Param("userId") Long userId, @Param("appointmentId") Long appointmentId);
}
