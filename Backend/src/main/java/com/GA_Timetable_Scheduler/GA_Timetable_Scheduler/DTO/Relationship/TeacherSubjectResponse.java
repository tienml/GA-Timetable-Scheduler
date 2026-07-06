package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TeacherSubjectResponse {

    private Integer id;

    private Integer teacherId;
    private String teacherCode;
    private String teacherName;

    private Integer subjectId;
    private String subjectCode;
    private String subjectName;
}