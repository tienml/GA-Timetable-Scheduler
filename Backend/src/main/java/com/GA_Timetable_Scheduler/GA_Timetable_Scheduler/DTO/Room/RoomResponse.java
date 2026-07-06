package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class RoomResponse {

    private Integer id;
    private String roomCode;
    private String roomName;
    private Integer capacity;
    private String building;
    private Integer floorNumber;
    private LocalDateTime createdAt;
}