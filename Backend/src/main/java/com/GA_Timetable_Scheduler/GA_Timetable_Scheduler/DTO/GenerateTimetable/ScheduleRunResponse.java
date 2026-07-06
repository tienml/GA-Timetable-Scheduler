package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
public class ScheduleRunResponse {

    private Integer id;
    private String runName;
    private Integer populationSize;
    private Integer generations;
    private Double mutationRate;
    private Double crossoverRate;
    private Integer totalPenalty;
    private Integer hardConstraintViolations;
    private Integer softConstraintViolations;
    private String status;
    private LocalDateTime createdAt;
}