package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TimetableEntryResponse {

    private Integer id;

    private Integer scheduleRunId;

    private Integer courseClassId;
    private String classCode;

    private Integer subjectId;
    private String subjectCode;
    private String subjectName;

    private Integer teacherId;
    private String teacherCode;
    private String teacherName;

    private Integer studentGroupId;
    private String studentGroupCode;
    private String studentGroupName;

    private Integer roomId;
    private String roomCode;
    private String roomName;
    private String building;

    private Integer timeSlotId;
    private Integer dayOfWeek;
    private String dayName;
    private Integer slotIndex;
    private String startTime;
    private String endTime;
    private String description;

    private Integer penalty;
}