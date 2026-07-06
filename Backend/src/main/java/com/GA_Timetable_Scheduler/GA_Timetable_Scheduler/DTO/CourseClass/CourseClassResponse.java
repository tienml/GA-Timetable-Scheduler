package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
public class CourseClassResponse {

    private Integer id;
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

    private Integer numberOfStudents;
    private Integer periods;
    private String note;
    private LocalDateTime createdAt;
}