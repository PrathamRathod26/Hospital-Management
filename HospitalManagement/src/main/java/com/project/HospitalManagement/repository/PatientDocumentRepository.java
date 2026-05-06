package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.Records.DocumentDto;
import com.project.HospitalManagement.entity.PatientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientDocumentRepository extends JpaRepository<PatientDocument, Long> {

    @NativeQuery(value = """
            select pd.* from patient_document pd join patient_data p on p.id = pd.patient_id where pd.id = :documentId and p.user_id = :userId;
            """)
    Optional<PatientDocument> findByIdAndUserId(
            @Param("userId") Long userId,
            @Param("documentId") Long documentId
    );

    @NativeQuery(value = """
            select * from patient_document where id = :documentId
            """)
    Optional<PatientDocument> findById(
            @Param("userId") Long userId,
            @Param("documentId") Long documentId
    );

    @NativeQuery(value = """
            select pd.id, pd.file_name from patient_document pd join patient_data p on pd.patient_id = p.id where p.user_id = :userId
            """)
    List<DocumentDto.response> findByUserId(@Param("userId") Long userId);

    @NativeQuery(value = """
            select case when count(*) > 0 then true else false end
            from patient_document pd
            join patient_data p on pd.patient_id = p.id
            where p.user_id = :userId and pd.id = :documentId
            """)
    boolean existsByIdAndUserId(@Param("documentId") Long documentId,@Param("userId") Long userId);

    @NativeQuery(value = """
            select pd.id, pd.file_name from patient_document pd
            join appointment_document ad on ad.patient_document_id = pd.id
            join appointment a on a.id = ad.appointment_id
            where a.id = :appointmentId
            """)
    List<DocumentDto.response> getAttachedDocumentAsAuthenticated(@Param("appointmentId") Long appointmentId);
}
