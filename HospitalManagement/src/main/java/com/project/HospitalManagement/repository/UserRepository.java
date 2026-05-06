package com.project.HospitalManagement.repository;

import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.patientProfile p
            LEFT JOIN FETCH p.appointments a
            LEFT JOIN FETCH p.documents d
            LEFT JOIN FETCH a.appointmentDocuments ad
            WHERE u.id = :id
        """)
    Optional<User> findPatientEverything(@Param("id") long id);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.doctorProfile d
            LEFT JOIN FETCH d.schedule ds
            LEFT JOIN FETCH d.slots s
            LEFT JOIN FETCH s.appointments a
            LEFT JOIN FETCH a.appointmentDocuments ad
            WHERE u.id = :id
        """)
    Optional<User> findDoctorEverything(@Param("id") long id);

    Optional<User> findByEmailAndRole(String email, Role role);

    boolean existsByEmailAndRole(String email, Role role);

    // all User data
    @NativeQuery("""
            select
            	json_agg(
            		json_build_object(
            			'userId', u.id,
            			'userEmail', u.email,
            			'userRole', u.role,
            			'patientId', p.id,
            			'doctorId', d.id
            		)
            	)
            from user_auth u
            left join patient_data p
            on p.user_id = u.id
            left join doctor_data d
            on d.user_id = u.id
            """)
    String getAllUser();

    @Query(value = """
            SELECT u FROM User u
            LEFT JOIN FETCH u.doctorProfile d
            LEFT JOIN FETCH u.patientProfile p
            LEFT JOIN FETCH p.appointments pa
            WHERE u.id = :userId
            """)
    User getEverythingByUser(@Param("userId") Long userId);
}
