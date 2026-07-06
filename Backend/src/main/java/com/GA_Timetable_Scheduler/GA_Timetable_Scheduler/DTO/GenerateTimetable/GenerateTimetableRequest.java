package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GenerateTimetableRequest {

    @Size(max = 100, message = "Tên lần chạy không được vượt quá 100 ký tự")
    private String runName;

    @NotNull(message = "Kích thước quần thể không được để trống")
    @Min(value = 10, message = "Kích thước quần thể phải tối thiểu là 10")
    private Integer populationSize = 100;

    @NotNull(message = "Số thế hệ không được để trống")
    @Min(value = 10, message = "Số thế hệ phải tối thiểu là 10")
    private Integer generations = 500;

    @NotNull(message = "Tỷ lệ đột biến không được để trống")
    @DecimalMin(value = "0.0", message = "Tỷ lệ đột biến phải từ 0 đến 1")
    @DecimalMax(value = "1.0", message = "Tỷ lệ đột biến phải từ 0 đến 1")
    private Double mutationRate = 0.05;

    @NotNull(message = "Tỷ lệ lai ghép không được để trống")
    @DecimalMin(value = "0.0", message = "Tỷ lệ lai ghép phải từ 0 đến 1")
    @DecimalMax(value = "1.0", message = "Tỷ lệ lai ghép phải từ 0 đến 1")
    private Double crossoverRate = 0.8;
}