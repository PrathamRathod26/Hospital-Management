package com.project.HospitalManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "prescription")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    @JsonIgnore
    private Appointment appointment;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "medication_notes")
    private String medicationNotes;

    @Column(name = "advice")
    private String advice;

    @CreationTimestamp
    private Timestamp createdAt;
}
