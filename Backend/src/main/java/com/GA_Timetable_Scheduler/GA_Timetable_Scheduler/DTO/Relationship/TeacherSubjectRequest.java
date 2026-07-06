package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TeacherSubjectRequest {

    @NotNull(message = "Giáo viên không được để trống")
    private Integer teacherId;

    @NotNull(message = "Môn học không được để trống")
    private Integer subjectId;
}