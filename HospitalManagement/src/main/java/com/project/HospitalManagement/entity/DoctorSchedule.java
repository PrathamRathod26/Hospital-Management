package com.project.HospitalManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "doctor_schedule")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private Doctor doctor;

    @Column(name = "work_days")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> daysOfWeek;

    @Column(name = "work_start_time")
    private LocalTime workStartTime;

    @Column(name = "work_end_time")
    private LocalTime workEndTime;

    @Column(name = "slot_duration")
    private Integer slotDuration;

    @Column(name = "default_slot_capacity")
    private Integer defaultSlotCapacity;
}
