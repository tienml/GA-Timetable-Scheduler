package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter @Setter @Builder
public class TimeSlotResponse {

    private Integer id;
    private Integer dayOfWeek;
    private String dayName;
    private Integer slotIndex;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
}