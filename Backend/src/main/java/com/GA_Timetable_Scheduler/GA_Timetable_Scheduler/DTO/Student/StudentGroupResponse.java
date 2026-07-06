package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class StudentGroupResponse {

    private Integer id;
    private String groupCode;
    private String groupName;
    private Integer numberOfStudents;
    private LocalDateTime createdAt;
}