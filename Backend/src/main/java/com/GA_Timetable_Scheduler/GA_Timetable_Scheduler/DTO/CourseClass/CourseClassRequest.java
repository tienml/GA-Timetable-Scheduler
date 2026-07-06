package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CourseClassRequest {

    @NotBlank(message = "Mã lớp học phần không được để trống")
    @Size(max = 50, message = "Mã lớp học phần không được vượt quá 50 ký tự")
    private String classCode;

    @NotNull(message = "Môn học không được để trống")
    private Integer subjectId;

    @NotNull(message = "Giáo viên không được để trống")
    private Integer teacherId;

    @NotNull(message = "Nhóm sinh viên không được để trống")
    private Integer studentGroupId;

    @NotNull(message = "Số lượng sinh viên không được để trống")
    @Min(value = 1, message = "Số lượng sinh viên phải lớn hơn 0")
    private Integer numberOfStudents;

    @NotNull(message = "Số tiết học không được để trống")
    @Min(value = 1, message = "Số tiết học phải lớn hơn 0")
    private Integer periods;

    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String note;
}