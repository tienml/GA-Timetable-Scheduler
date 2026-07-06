package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoomRequest {

    @NotBlank(message = "Mã phòng học không được để trống")
    @Size(max = 50, message = "Mã phòng học không được vượt quá 50 ký tự")
    private String roomCode;

    @NotBlank(message = "Tên phòng học không được để trống")
    @Size(max = 100, message = "Tên phòng học không được vượt quá 100 ký tự")
    private String roomName;

    @NotNull(message = "Sức chứa phòng học không được để trống")
    @Min(value = 1, message = "Sức chứa phòng học phải lớn hơn 0")
    private Integer capacity;

    @Size(max = 100, message = "Tên tòa nhà không được vượt quá 100 ký tự")
    private String building;

    @Min(value = 0, message = "Số tầng không được nhỏ hơn 0")
    private Integer floorNumber;
}