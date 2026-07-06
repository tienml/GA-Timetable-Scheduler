package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class TeacherResponse {

    private Integer id;
    private String teacherCode;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}