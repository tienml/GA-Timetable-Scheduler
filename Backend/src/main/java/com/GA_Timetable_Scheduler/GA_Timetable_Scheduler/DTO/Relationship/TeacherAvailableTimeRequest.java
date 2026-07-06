package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TeacherAvailableTimeRequest {

    @NotNull(message = "Giáo viên không được để trống")
    private Integer teacherId;

    @NotNull(message = "Khung giờ không được để trống")
    private Integer timeSlotId;
}