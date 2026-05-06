package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @NativeQuery(value = """
            select id, first_name, last_name, phone, specialization, license_number from doctor_data where user_id=:userId
            """)
    Optional<DoctorDto.dbResponse> v1_findDoctorByUserId(@Param("userId") long userId);

    @NativeQuery(value = """
            select filter_doctor(:name, :specialization)
            """)
    String v1_getFilteredDoctor(@Param("name") String name, @Param("specialization") String specialization);

    @NativeQuery(value = """
            SELECT
            	id,
            	first_name,
            	last_name,
            	phone,
            	specialization,
            	license_number
            FROM doctor_data
            WHERE
            	(:nameFilter IS NULL OR (first_name ILIKE '%' || :nameFilter || '%' OR last_name ILIKE '%' || :nameFilter || '%'))
            	AND
            	(:specializationFilter IS NULL OR specialization ILIKE '%' || :specializationFilter || '%')
            """)
    List<DoctorDto.response> v3_getFilteredDoctor(@Param("nameFilter") String name, @Param("specializationFilter") String specialization);

    Optional<Doctor> findByUserId(long userId);

    @NativeQuery(value = """
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
                        SELECT jsonb_agg(slot_obj)
                        FROM (
                            SELECT jsonb_build_object(
                                'id', s.id,
                                'date', s.slot_date,
                                'startTime', s.start_time,
                                'endTime', s.end_time,
                                'slotCapacity', s.slot_capacity,
                                'bookedCount', s.booked_count,
                                'status', s.status
                            ) AS slot_obj
                            FROM slot_data s
                            WHERE s.doctor_id = d.id
                            ORDER BY s.slot_date DESC , s.start_time DESC
                        ) sub
                    ),
            		'appointments', (
            		    SELECT jsonb_agg(app_obj)
            		    FROM (
            		        SELECT jsonb_build_object(
            		            'id', a.id,
            		            'status', a.status,
            		            'reason', a.reason,
            		            'notes', a.notes,
            		            'date', s.slot_date,
            		            'startTime', s.start_time,
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
            		        ) AS app_obj
            		        FROM appointment a
            		        JOIN slot_data s ON a.slot_id = s.id
            		        JOIN patient_data p ON a.patient_id = p.id
            		        WHERE s.doctor_id = d.id
            		        ORDER BY s.slot_date DESC, s.start_time DESC
            		        LIMIT 10
            		    ) sub
            		)
            	)
            FROM doctor_data d
            WHERE d.user_id = :userId;
            """)
    String getDoctorProfile(Long userId);
}
