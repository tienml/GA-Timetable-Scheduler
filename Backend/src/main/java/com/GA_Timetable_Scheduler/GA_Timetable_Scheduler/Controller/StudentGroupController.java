package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student.StudentGroupRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student.StudentGroupResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.StudentGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-groups")
@RequiredArgsConstructor
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    @GetMapping
    public ApiResponse<List<StudentGroupResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách nhóm sinh viên thành công", studentGroupService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<StudentGroupResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin nhóm sinh viên thành công", studentGroupService.getById(id));
    }

    @PostMapping
    public ApiResponse<StudentGroupResponse> create(@Valid @RequestBody StudentGroupRequest request) {
        return ApiResponse.success("Tạo nhóm sinh viên thành công", studentGroupService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<StudentGroupResponse> update(@PathVariable Integer id, @Valid @RequestBody StudentGroupRequest request) {
        return ApiResponse.success("Cập nhật nhóm sinh viên thành công", studentGroupService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        studentGroupService.delete(id);
        return ApiResponse.success("Xóa nhóm sinh viên thành công", null);
    }
}
