package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TeacherAvailableTimeResponse {

    private Integer id;

    private Integer teacherId;
    private String teacherCode;
    private String teacherName;

    private Integer timeSlotId;
    private Integer dayOfWeek;
    private String dayName;
    private Integer slotIndex;
    private String startTime;
    private String endTime;
    private String description;
}