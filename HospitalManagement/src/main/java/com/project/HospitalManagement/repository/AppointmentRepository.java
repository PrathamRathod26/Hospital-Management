package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.Records.AppointmentDto;
import com.project.HospitalManagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @NativeQuery(value = """
            select
            	a.*
            from appointment a
            join slot_data s on s.id = a.slot_id
            join doctor_data d on d.id=s.doctor_id
            where
            	a.id = :appointmentId
            	and
            	d.user_id = :userId
            """)
    Optional<Appointment> findByIdAndDoctorUserId(@Param("appointmentId") Long appointmentId, @Param("userId") Long userId);

    @NativeQuery(value = """
            select
            	a.*
            from appointment a
            join patient_data p on p.id=a.patient_id
            where
            	a.id = :appointmentId
            	and
            	p.user_id = :userId
            """)
    Optional<Appointment> findByIdAndPatientUserId(@Param("appointmentId") Long appointmentId, @Param("userId") Long userId);

    @NativeQuery(value = """
            select
            	p.id, p.first_name, p.last_name, p.phone, p.age, p.gender, p.blood_group, p.emergency_contact,
            	d.id, d.first_name, d.last_name, d.phone, d.specialization, d.license_number,
            	s.id, s.slot_date, s.start_time, s.end_time, s.slot_capacity, s.booked_count, s.status,
            	a.id, a.status, a.reason, a.notes
            from appointment a
            join patient_data p on a.patient_id = p.id
            join slot_data s on a.slot_id = s.id
            join doctor_data d on s.doctor_id = d.id
            where d.user_id=:userId
            """)
    List<AppointmentDto.dbResponse> v1_findAllDoctorAppointmentsByUserId(@Param("userId") long userId);

    @NativeQuery(value = """
            select
            	p.id, p.first_name, p.last_name, p.phone, p.age, p.gender, p.blood_group, p.emergency_contact,
            	d.id, d.first_name, d.last_name, d.phone, d.specialization, d.license_number,
            	s.id, s.slot_date, s.start_time, s.end_time, s.slot_capacity, s.booked_count, s.status,
            	a.id, a.status, a.reason, a.notes
            from appointment a
            join patient_data p on a.patient_id = p.id
            join slot_data s on a.slot_id = s.id
            join doctor_data d on s.doctor_id = d.id
            where p.user_id=:userId
            """)
    List<AppointmentDto.dbResponse> v1_findAllPatientAppointmentsByUserId(@Param("userId") long userId);

    @NativeQuery(value = """
            SELECT get_doctor_appointments_json(:userId)
            """)
    String v2_findAllDoctorAppointmentsByUserIdJson(@Param("userId") long userId);

    @NativeQuery(value = """
            SELECT get_patient_appointments_json(:userId)
            """)
    String v2_findAllPatientAppointmentsByUserIdJson(@Param("userId") long userId);

    @NativeQuery(value = """
            SELECT
                jsonb_build_object(
                    'patient', jsonb_build_object(
                        'id', p.id,
                        'firstName', p.first_name,
                        'lastName', p.last_name,
                        'phone', p.phone,
                        'age', p.age,
                        'gender', p.gender,
                        'bloodGroup', p.blood_group,
                        'emergencyContact', p.emergency_contact
                    ),
                    'appointments', (
                        SELECT (jsonb_agg(app_obj))
                        FROM (
                            SELECT jsonb_build_object(
                                'id', a.id,
                                'status', a.status,
                                'reason', a.reason,
                                'notes', a.notes,
                                'date', s.slot_date,
                                'startTime', s.start_time,
                                'doctor', jsonb_build_object(
                                    'id', d.id,
                                    'firstName', d.first_name,
                                    'lastName', d.last_name,
                                    'phone', d.phone,
                                    'specialization', d.specialization,
                                    'licenseNumber', d.license_number
                                ),
                                'prescription',
                                    CASE
                                        WHEN pr.id IS NOT NULL THEN jsonb_build_object(
                                            'id', pr.id,
                                            'diagnosis', pr.diagnosis,
                                            'medicationNotes', pr.medication_notes,
                                            'advice', pr.advice
                                        )
                                    ELSE NULL
                                END
                            ) AS app_obj
                            FROM appointment a
                            JOIN slot_data s ON s.id = a.slot_id
                            LEFT JOIN doctor_data d ON d.id = s.doctor_id
                            LEFT JOIN prescription pr on pr.appointment_id = a.id
                            WHERE a.patient_id = p.id
                            ORDER BY s.slot_date DESC , s.start_time DESC
                        ) AS sub
                    )
                )
            FROM patient_data p
            WHERE p.user_id = :userId;
            """)
    String v3_getPatientAppointments(@Param("userId") long userId);


    @NativeQuery("""
            SELECT
                jsonb_build_object(
                    'doctor', jsonb_build_object(
                        'id', d.id,
                        'firstName', d.first_name,
                        'lastName', d.last_name,
                        'phone', d.phone,
                        'specialization', d.specialization,
                        'licenseNumber', d.license_number
                    ),
                    'slots', (
                        SELECT jsonb_agg(
                            jsonb_build_object(
                                'id', s.id,
                                'date', s.slot_date,
                                'startTime', s.start_time,
                                'endTime', s.end_time,
                                'capacity', s.slot_capacity,
                                'bookedCount', s.booked_count,
                                'status', s.status,
                                'appointments', (
                                    SELECT jsonb_agg(
                                        jsonb_build_object(
                                            'id', a.id,
                                            'status', a.status,
                                            'reason', a.reason,
                                            'notes', a.notes,
                                            'patient', jsonb_build_object(
                                                'id', p.id,
                                                'firstName', p.first_name,
                                                'lastName', p.last_name,
                                                'phone', p.phone,
                                                'age', p.age,
                                                'gender', p.gender,
                                                'bloodGroup', p.blood_group,
                                                'emergencyContact', p.emergency_contact
                                            )
                                        )
                                        ORDER BY a.id
                                    )
                                    FROM appointment a
                                    JOIN patient_data p ON p.id = a.patient_id
                                    WHERE a.slot_id = s.id
                                )
                            )
                            ORDER BY s.slot_date, s.start_time
                        )
                        FROM slot_data s
                        WHERE s.doctor_id = d.id
                          AND EXISTS (
                              SELECT 1 FROM appointment a2 WHERE a2.slot_id = s.id
                          )
                    )
                )
            FROM doctor_data d
            WHERE d.user_id = :userId;
            """)
    String v3_getDoctorAppointments(@Param("userId") long userId);
}
