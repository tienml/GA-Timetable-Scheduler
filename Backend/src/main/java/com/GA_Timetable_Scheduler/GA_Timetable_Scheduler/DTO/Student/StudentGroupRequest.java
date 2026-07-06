package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentGroupRequest {

    @NotBlank(message = "Mã nhóm sinh viên không được để trống")
    @Size(max = 50, message = "Mã nhóm sinh viên không được vượt quá 50 ký tự")
    private String groupCode;

    @NotBlank(message = "Tên nhóm sinh viên không được để trống")
    @Size(max = 100, message = "Tên nhóm sinh viên không được vượt quá 100 ký tự")
    private String groupName;

    @NotNull(message = "Số lượng sinh viên không được để trống")
    @Min(value = 1, message = "Số lượng sinh viên phải lớn hơn 0")
    private Integer numberOfStudents;
}