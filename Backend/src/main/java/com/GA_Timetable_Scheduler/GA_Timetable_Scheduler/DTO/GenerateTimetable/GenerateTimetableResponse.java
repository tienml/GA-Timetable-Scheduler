package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Builder
public class GenerateTimetableResponse {

    private ScheduleRunResponse scheduleRun;
    private List<TimetableEntryResponse> timetableEntries;
}