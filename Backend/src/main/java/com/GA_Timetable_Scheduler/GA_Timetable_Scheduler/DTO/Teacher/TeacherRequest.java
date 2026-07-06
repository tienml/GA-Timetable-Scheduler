package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TeacherRequest {

    @NotBlank(message = "Mã giáo viên không được để trống")
    @Size(max = 50, message = "Mã giáo viên không được vượt quá 50 ký tự")
    private String teacherCode;

    @NotBlank(message = "Họ tên giáo viên không được để trống")
    @Size(max = 100, message = "Họ tên giáo viên không được vượt quá 100 ký tự")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    private String phone;
}