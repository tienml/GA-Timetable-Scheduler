package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoomEquipmentRequest {

    @NotNull(message = "Phòng học không được để trống")
    private Integer roomId;

    @NotNull(message = "Thiết bị không được để trống")
    private Integer equipmentId;
}