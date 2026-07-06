package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Controller;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.ApiResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.*;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service.RelationshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @GetMapping("/api/teacher-subjects")
    public ApiResponse<List<TeacherSubjectResponse>> getTeacherSubjects() {
        return ApiResponse.success("Lấy danh sách giáo viên - môn học thành công", relationshipService.getAllTeacherSubjects());
    }

    @PostMapping("/api/teacher-subjects")
    public ApiResponse<TeacherSubjectResponse> createTeacherSubject(@Valid @RequestBody TeacherSubjectRequest request) {
        return ApiResponse.success("Gán môn học cho giáo viên thành công", relationshipService.createTeacherSubject(request));
    }

    @DeleteMapping("/api/teacher-subjects/{id}")
    public ApiResponse<Void> deleteTeacherSubject(@PathVariable Integer id) {
        relationshipService.deleteTeacherSubject(id);
        return ApiResponse.success("Xóa dữ liệu giáo viên - môn học thành công", null);
    }

    @GetMapping("/api/teacher-available-times")
    public ApiResponse<List<TeacherAvailableTimeResponse>> getTeacherAvailableTimes() {
        return ApiResponse.success("Lấy danh sách thời gian rảnh của giáo viên thành công", relationshipService.getAllTeacherAvailableTimes());
    }

    @PostMapping("/api/teacher-available-times")
    public ApiResponse<TeacherAvailableTimeResponse> createTeacherAvailableTime(@Valid @RequestBody TeacherAvailableTimeRequest request) {
        return ApiResponse.success("Thêm thời gian rảnh cho giáo viên thành công", relationshipService.createTeacherAvailableTime(request));
    }

    @DeleteMapping("/api/teacher-available-times/{id}")
    public ApiResponse<Void> deleteTeacherAvailableTime(@PathVariable Integer id) {
        relationshipService.deleteTeacherAvailableTime(id);
        return ApiResponse.success("Xóa thời gian rảnh của giáo viên thành công", null);
    }

    @GetMapping("/api/teacher-preferred-times")
    public ApiResponse<List<TeacherPreferredTimeResponse>> getTeacherPreferredTimes() {
        return ApiResponse.success("Lấy danh sách thời gian mong muốn của giáo viên thành công", relationshipService.getAllTeacherPreferredTimes());
    }

    @PostMapping("/api/teacher-preferred-times")
    public ApiResponse<TeacherPreferredTimeResponse> createTeacherPreferredTime(@Valid @RequestBody TeacherPreferredTimeRequest request) {
        return ApiResponse.success("Thêm thời gian mong muốn cho giáo viên thành công", relationshipService.createTeacherPreferredTime(request));
    }

    @DeleteMapping("/api/teacher-preferred-times/{id}")
    public ApiResponse<Void> deleteTeacherPreferredTime(@PathVariable Integer id) {
        relationshipService.deleteTeacherPreferredTime(id);
        return ApiResponse.success("Xóa thời gian mong muốn của giáo viên thành công", null);
    }

    @GetMapping("/api/room-available-times")
    public ApiResponse<List<RoomAvailableTimeResponse>> getRoomAvailableTimes() {
        return ApiResponse.success("Lấy danh sách thời gian trống của phòng thành công", relationshipService.getAllRoomAvailableTimes());
    }

    @PostMapping("/api/room-available-times")
    public ApiResponse<RoomAvailableTimeResponse> createRoomAvailableTime(@Valid @RequestBody RoomAvailableTimeRequest request) {
        return ApiResponse.success("Thêm thời gian trống cho phòng thành công", relationshipService.createRoomAvailableTime(request));
    }

    @DeleteMapping("/api/room-available-times/{id}")
    public ApiResponse<Void> deleteRoomAvailableTime(@PathVariable Integer id) {
        relationshipService.deleteRoomAvailableTime(id);
        return ApiResponse.success("Xóa thời gian trống của phòng thành công", null);
    }

    @GetMapping("/api/room-equipment")
    public ApiResponse<List<RoomEquipmentResponse>> getRoomEquipment() {
        return ApiResponse.success("Lấy danh sách thiết bị của phòng thành công", relationshipService.getAllRoomEquipment());
    }

    @PostMapping("/api/room-equipment")
    public ApiResponse<RoomEquipmentResponse> createRoomEquipment(@Valid @RequestBody RoomEquipmentRequest request) {
        return ApiResponse.success("Gán thiết bị cho phòng thành công", relationshipService.createRoomEquipment(request));
    }

    @DeleteMapping("/api/room-equipment/{id}")
    public ApiResponse<Void> deleteRoomEquipment(@PathVariable Integer id) {
        relationshipService.deleteRoomEquipment(id);
        return ApiResponse.success("Xóa thiết bị của phòng thành công", null);
    }

    @GetMapping("/api/course-class-required-equipment")
    public ApiResponse<List<CourseClassRequiredEquipmentResponse>> getCourseClassRequiredEquipment() {
        return ApiResponse.success("Lấy danh sách thiết bị yêu cầu của lớp học phần thành công", relationshipService.getAllCourseClassRequiredEquipment());
    }

    @PostMapping("/api/course-class-required-equipment")
    public ApiResponse<CourseClassRequiredEquipmentResponse> createCourseClassRequiredEquipment(@Valid @RequestBody CourseClassRequiredEquipmentRequest request) {
        return ApiResponse.success("Gán thiết bị yêu cầu cho lớp học phần thành công", relationshipService.createCourseClassRequiredEquipment(request));
    }

    @DeleteMapping("/api/course-class-required-equipment/{id}")
    public ApiResponse<Void> deleteCourseClassRequiredEquipment(@PathVariable Integer id) {
        relationshipService.deleteCourseClassRequiredEquipment(id);
        return ApiResponse.success("Xóa thiết bị yêu cầu của lớp học phần thành công", null);
    }
}
