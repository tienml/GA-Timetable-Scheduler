package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EquipmentResponse {

    private Integer id;
    private String equipmentCode;
    private String equipmentName;
}