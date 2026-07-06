package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class CourseClassRequiredEquipmentResponse {

    private Integer id;

    private Integer courseClassId;
    private String classCode;

    private Integer equipmentId;
    private String equipmentCode;
    private String equipmentName;
}