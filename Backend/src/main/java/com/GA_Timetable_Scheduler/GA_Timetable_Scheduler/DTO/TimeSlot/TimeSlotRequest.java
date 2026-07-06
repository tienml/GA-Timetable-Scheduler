package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter @Setter
public class TimeSlotRequest {

    @NotNull(message = "Thứ trong tuần không được để trống")
    @Min(value = 2, message = "Thứ trong tuần phải từ 2 đến 7")
    @Max(value = 7, message = "Thứ trong tuần phải từ 2 đến 7")
    private Integer dayOfWeek;

    @NotNull(message = "Ca học không được để trống")
    @Min(value = 1, message = "Ca học phải lớn hơn 0")
    private Integer slotIndex;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalTime endTime;

    @Size(max = 100, message = "Mô tả khung giờ không được vượt quá 100 ký tự")
    private String description;
}