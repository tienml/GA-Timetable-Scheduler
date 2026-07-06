package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot.TimeSlotRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot.TimeSlotResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping
    public ApiResponse<List<TimeSlotResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách khung giờ thành công", timeSlotService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<TimeSlotResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin khung giờ thành công", timeSlotService.getById(id));
    }

    @PostMapping
    public ApiResponse<TimeSlotResponse> create(@Valid @RequestBody TimeSlotRequest request) {
        return ApiResponse.success("Tạo khung giờ thành công", timeSlotService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TimeSlotResponse> update(@PathVariable Integer id, @Valid @RequestBody TimeSlotRequest request) {
        return ApiResponse.success("Cập nhật khung giờ thành công", timeSlotService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        timeSlotService.delete(id);
        return ApiResponse.success("Xóa khung giờ thành công", null);
    }
}
