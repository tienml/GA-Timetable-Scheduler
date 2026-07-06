package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubjectRequest {

    @NotBlank(message = "Mã môn học không được để trống")
    @Size(max = 50, message = "Mã môn học không được vượt quá 50 ký tự")
    private String subjectCode;

    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 150, message = "Tên môn học không được vượt quá 150 ký tự")
    private String subjectName;

    @NotNull(message = "Số tiết mặc định không được để trống")
    @Min(value = 1, message = "Số tiết mặc định phải lớn hơn 0")
    private Integer defaultPeriods;
}