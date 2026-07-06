package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher.TeacherRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher.TeacherResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ApiResponse<List<TeacherResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách giáo viên thành công", teacherService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<TeacherResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin giáo viên thành công", teacherService.getById(id));
    }

    @PostMapping
    public ApiResponse<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ApiResponse.success("Tạo giáo viên thành công", teacherService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TeacherResponse> update(@PathVariable Integer id, @Valid @RequestBody TeacherRequest request) {
        return ApiResponse.success("Cập nhật giáo viên thành công", teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        teacherService.delete(id);
        return ApiResponse.success("Xóa giáo viên thành công", null);
    }
}
