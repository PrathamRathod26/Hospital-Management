package com.project.HospitalManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.HospitalManagement.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "slot_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id", "slot_date", "start_time"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnore
    private Doctor doctor;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL)
    private Set<Appointment> appointments = new HashSet<>();

    @Column(name = "slot_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_capacity", nullable = false)
    private Integer slotCapacity;

    @Column(name = "bookedCount", nullable = false)
    private Integer bookedCount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SlotStatus status;
}
