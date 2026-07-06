package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class SubjectResponse {

    private Integer id;
    private String subjectCode;
    private String subjectName;
    private Integer defaultPeriods;
    private LocalDateTime createdAt;
}