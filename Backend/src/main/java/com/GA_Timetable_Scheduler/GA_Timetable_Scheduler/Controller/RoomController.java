package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room.RoomRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room.RoomResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ApiResponse<List<RoomResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách phòng học thành công", roomService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin phòng học thành công", roomService.getById(id));
    }

    @PostMapping
    public ApiResponse<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        return ApiResponse.success("Tạo phòng học thành công", roomService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> update(@PathVariable Integer id, @Valid @RequestBody RoomRequest request) {
        return ApiResponse.success("Cập nhật phòng học thành công", roomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        roomService.delete(id);
        return ApiResponse.success("Xóa phòng học thành công", null);
    }
}
