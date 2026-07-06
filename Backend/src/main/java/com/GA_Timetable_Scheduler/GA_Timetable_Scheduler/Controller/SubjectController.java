package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject.SubjectRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject.SubjectResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ApiResponse<List<SubjectResponse>> getAll() {
        return ApiResponse.success("Lấy danh sách môn học thành công", subjectService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SubjectResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success("Lấy thông tin môn học thành công", subjectService.getById(id));
    }

    @PostMapping
    public ApiResponse<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        return ApiResponse.success("Tạo môn học thành công", subjectService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SubjectResponse> update(@PathVariable Integer id, @Valid @RequestBody SubjectRequest request) {
        return ApiResponse.success("Cập nhật môn học thành công", subjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        subjectService.delete(id);
        return ApiResponse.success("Xóa môn học thành công", null);
    }
}
