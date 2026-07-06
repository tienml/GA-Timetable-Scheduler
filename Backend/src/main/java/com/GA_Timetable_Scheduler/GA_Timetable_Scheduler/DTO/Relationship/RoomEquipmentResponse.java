package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class RoomEquipmentResponse {

    private Integer id;

    private Integer roomId;
    private String roomCode;
    private String roomName;

    private Integer equipmentId;
    private String equipmentCode;
    private String equipmentName;
}