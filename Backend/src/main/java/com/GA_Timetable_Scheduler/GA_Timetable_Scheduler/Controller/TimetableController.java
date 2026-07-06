package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.GenerateTimetableRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.GenerateTimetableResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.ScheduleRunResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/generate")
    public ApiResponse<GenerateTimetableResponse> generate(@Valid @RequestBody GenerateTimetableRequest request) {
        return ApiResponse.success("Tạo thời khóa biểu thành công", timetableService.generate(request));
    }

    @GetMapping("/latest")
    public ApiResponse<GenerateTimetableResponse> getLatest() {
        return ApiResponse.success("Lấy thời khóa biểu mới nhất thành công", timetableService.getLatest());
    }

    @GetMapping("/runs")
    public ApiResponse<List<ScheduleRunResponse>> getAllRuns() {
        return ApiResponse.success("Lấy danh sách lần chạy thành công", timetableService.getAllRuns());
    }

    @GetMapping("/runs/{runId}")
    public ApiResponse<GenerateTimetableResponse> getByRunId(@PathVariable Integer runId) {
        return ApiResponse.success("Lấy thời khóa biểu theo lần chạy thành công", timetableService.getByRunId(runId));
    }
}
