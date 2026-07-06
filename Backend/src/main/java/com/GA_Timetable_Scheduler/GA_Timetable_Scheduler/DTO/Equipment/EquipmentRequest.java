package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EquipmentRequest {

    @NotBlank(message = "Mã thiết bị không được để trống")
    @Size(max = 50, message = "Mã thiết bị không được vượt quá 50 ký tự")
    private String equipmentCode;

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(max = 100, message = "Tên thiết bị không được vượt quá 100 ký tự")
    private String equipmentName;
}