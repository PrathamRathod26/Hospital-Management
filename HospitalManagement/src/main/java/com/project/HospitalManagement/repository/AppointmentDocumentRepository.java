package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.entity.AppointmentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentDocumentRepository extends JpaRepository<AppointmentDocument, Long> {

    @Modifying
    @Query(value = """
            DELETE FROM appointment_document ad
            WHERE ad.appointment_id = :appointmentId
            AND EXISTS (
                SELECT 1
                FROM patient_document pd
                WHERE pd.id = :patientDocumentId
                AND pd.patient_id = :patientId
                AND pd.id = ad.patient_document_id
            )
            """, nativeQuery = true)
    int removeDocumentFromDraft(
            @Param("appointmentId") Long appointmentId,
            @Param("patientId") Long patientId,
            @Param("patientDocumentId") Long patientDocumentId
    );
}
