package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TeacherPreferredTimeRequest {

    @NotNull(message = "Giáo viên không được để trống")
    private Integer teacherId;

    @NotNull(message = "Khung giờ không được để trống")
    private Integer timeSlotId;

    @NotNull(message = "Điểm ưu tiên không được để trống")
    @Min(value = 1, message = "Điểm ưu tiên phải lớn hơn 0")
    private Integer priorityScore = 10;
}
