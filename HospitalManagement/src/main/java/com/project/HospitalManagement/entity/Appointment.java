package com.project.HospitalManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.HospitalManagement.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "appointment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"patient_id","slot_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = true)
    @JsonIgnore
    private Slot slot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = true)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Prescription prescription;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Set<AppointmentDocument> appointmentDocuments;

    @Column(nullable = false)
    private boolean draft = false;
}
