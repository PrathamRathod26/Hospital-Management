package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @NativeQuery(value = """
            select id,first_name,last_name,phone,age,gender,blood_group,emergency_contact from patient_data where user_id=:userId;
            """)
    Optional<PatientDto.dbResponse> v1_findPatientByUserId(@Param("userId") long userId);

    Optional<Patient> findByUserId(Long id);

    @NativeQuery("""
            select
            	json_build_object(
            		'patient', json_build_object(
            			'id', p.id,
            			'firstName', p.first_name,
            	        'lastName', p.last_name,
            	        'phone', p.phone,
            	        'age', p.age,
            	        'gender', p.gender,
            	        'bloodGroup', p.blood_group,
            	        'emergencyContact', p.emergency_contact
            		),
            		'documents', json_agg(
                    	json_build_object(
                        	'id', d.id,
                            'fileName', d.file_name
            			)
            		) filter (
                    	where d.id is not null
                	)
            	)
            from patient_data p
            left join patient_document d
            on p.id = d.patient_id
            where p.id = :patientId
            group by p.id
            """)
    String v1_findPatientDetails(@Param("patientId") Long patientId);

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
                    'appointment', jsonb_build_object(
                        'id', a.id,
                        'notes', a.notes,
                        'reason', a.reason,
                        'status', a.status,
                        'date', s.slot_date,
                        'startTime', s.start_time,
                        'prescription', (
                            SELECT jsonb_build_object(
                                'id', pre.id,
                                'diagnosis', pre.diagnosis,
                                'medicationNotes', pre.medication_notes,
                                'advice', pre.advice
                            )
                            FROM prescription pre
                            WHERE pre.appointment_id = a.id
                            LIMIT 1
                        )
                    ),
                    'documents', (
                        SELECT jsonb_agg(
                            jsonb_build_object(
                                'id', pd.id,
                                'fileName', pd.file_name,
                                'documentUrl', '/api/document/public/' ||  pd.id || '/thumbnail'
                            )
                        )
                        FROM appointment_document ad
                        JOIN patient_document pd ON ad.patient_document_id = pd.id
                        WHERE ad.appointment_id = a.id
                    )
                )
            FROM patient_data p
            JOIN appointment a ON a.patient_id = p.id
            LEFT JOIN slot_data s ON s.id = a.slot_id
            WHERE p.id = :patientId AND a.id = :appointmentId;
            """)
    String v2_findPatientDetails(@Param("patientId") Long patientId, @Param("appointmentId") Long appointmentId);

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
                   			SELECT jsonb_agg(app_obj)
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
                   				JOIN slot_data s ON a.slot_id = s.id
                   				LEFT JOIN doctor_data d ON s.doctor_id = d.id
                                LEFT JOIN prescription pr ON pr.appointment_id = a.id
                   				WHERE a.patient_id = p.id
                                ORDER BY s.slot_date DESC , s.start_time DESC
                   				LIMIT 10
                   			) sub
                   		),
                   		'documents', (
                   			SELECT jsonb_agg(doc_obj)
                   			FROM (
                   				SELECT jsonb_build_object(
                   					'id', d.id,
                   					'fileName', d.file_name,
                   					'documentUrl', '/api/document/public/' ||  d.id || '/thumbnail'
                   				) as doc_obj
                   				FROM patient_document d
                   				where d.patient_id = p.id
                   			) sub
                   		),
                   		'drafts', (
                               SELECT (jsonb_agg(draft_obj))
                     			    from (
                     				    SELECT jsonb_build_object(
                     					'id', a.id,
                     					'status', a.status,
                     					'reason', a.reason,
                     					'notes', a.notes,
                     					'draft', a.draft,
                                       'documents', (
                                            SELECT (jsonb_agg(draft_document_obj))
                                            FROM (
                                                SELECT Jsonb_build_object(
                                                    'id', pd.id,
                                                    'fileName', pd.file_name
                                                    ) AS draft_document_obj
                                                FROM appointment_document ad
                                                JOIN patient_document pd on pd.id = ad.patient_document_id
                                                WHERE ad.appointment_id = a.id
                                            ) sub
                                        )
                                    ) AS draft_obj
                     			FROM appointment a
                     			Where a.patient_id = p.id
                     		    AND a.draft = 'true'
                     	    ) sub
                        )
                   )
                from patient_data p
                where p.user_id = :userId;
            """)
    String getPatientProfile(@Param("userId") Long userId);

}
