package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "teacher_preferred_times")
public class TeacherPreferredTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "priority_score", nullable = false)
    private Integer priorityScore = 10;
}