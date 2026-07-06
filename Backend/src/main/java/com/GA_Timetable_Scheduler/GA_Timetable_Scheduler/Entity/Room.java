package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_code", nullable = false, unique = true, length = 50)
    private String roomCode;

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}