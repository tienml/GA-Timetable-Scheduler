package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CourseClassRequiredEquipmentRequest {

    @NotNull(message = "Lớp học phần không được để trống")
    private Integer courseClassId;

    @NotNull(message = "Thiết bị không được để trống")
    private Integer equipmentId;
}