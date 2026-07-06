package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment.EquipmentRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment.EquipmentResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ApiResponse<List<EquipmentResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách thiết bị thành công", equipmentService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<EquipmentResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin thiết bị thành công", equipmentService.getById(id));
    }

    @PostMapping
    public ApiResponse<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        return ApiResponse.success("Tạo thiết bị thành công", equipmentService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EquipmentResponse> update(@PathVariable Integer id, @Valid @RequestBody EquipmentRequest request) {
        return ApiResponse.success("Cập nhật thiết bị thành công", equipmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        equipmentService.delete(id);
        return ApiResponse.success("Xóa thiết bị thành công", null);
    }
}
