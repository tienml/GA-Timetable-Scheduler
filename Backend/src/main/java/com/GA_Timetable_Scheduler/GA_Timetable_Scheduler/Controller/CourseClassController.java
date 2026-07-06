package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass.CourseClassRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass.CourseClassResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.CourseClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-classes")
@RequiredArgsConstructor
public class CourseClassController {

    private final CourseClassService courseClassService;

    @GetMapping
    public ApiResponse<List<CourseClassResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách lớp học phần thành công", courseClassService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseClassResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin lớp học phần thành công", courseClassService.getById(id));
    }

    @PostMapping
    public ApiResponse<CourseClassResponse> create(@Valid @RequestBody CourseClassRequest request) {
        return ApiResponse.success("Tạo lớp học phần thành công", courseClassService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseClassResponse> update(@PathVariable Integer id, @Valid @RequestBody CourseClassRequest request) {
        return ApiResponse.success("Cập nhật lớp học phần thành công", courseClassService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        courseClassService.delete(id);
        return ApiResponse.success("Xóa lớp học phần thành công", null);
    }
}
