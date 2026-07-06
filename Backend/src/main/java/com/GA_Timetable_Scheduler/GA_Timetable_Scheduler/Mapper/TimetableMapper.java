package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass.CourseClassResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment.EquipmentResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.ScheduleRunResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.TimetableEntryResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.CourseClassRequiredEquipmentResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.RoomAvailableTimeResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.RoomEquipmentResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.TeacherAvailableTimeResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.TeacherPreferredTimeResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.TeacherSubjectResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room.RoomResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student.StudentGroupResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject.SubjectResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher.TeacherResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot.TimeSlotResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimetableMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TeacherResponse toTeacherResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .teacherCode(teacher.getTeacherCode())
                .fullName(teacher.getFullName())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .createdAt(teacher.getCreatedAt())
                .build();
    }

    public SubjectResponse toSubjectResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .defaultPeriods(subject.getDefaultPeriods())
                .createdAt(subject.getCreatedAt())
                .build();
    }

    public StudentGroupResponse toStudentGroupResponse(StudentGroup studentGroup) {
        return StudentGroupResponse.builder()
                .id(studentGroup.getId())
                .groupCode(studentGroup.getGroupCode())
                .groupName(studentGroup.getGroupName())
                .numberOfStudents(studentGroup.getNumberOfStudents())
                .createdAt(studentGroup.getCreatedAt())
                .build();
    }

    public RoomResponse toRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .roomName(room.getRoomName())
                .capacity(room.getCapacity())
                .building(room.getBuilding())
                .floorNumber(room.getFloorNumber())
                .createdAt(room.getCreatedAt())
                .build();
    }

    public EquipmentResponse toEquipmentResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .equipmentCode(equipment.getEquipmentCode())
                .equipmentName(equipment.getEquipmentName())
                .build();
    }

    public TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot) {
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .dayOfWeek(timeSlot.getDayOfWeek())
                .dayName(toDayName(timeSlot.getDayOfWeek()))
                .slotIndex(timeSlot.getSlotIndex())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .description(timeSlot.getDescription())
                .build();
    }

    public CourseClassResponse toCourseClassResponse(CourseClass courseClass) {
        return CourseClassResponse.builder()
                .id(courseClass.getId())
                .classCode(courseClass.getClassCode())
                .subjectId(courseClass.getSubject().getId())
                .subjectCode(courseClass.getSubject().getSubjectCode())
                .subjectName(courseClass.getSubject().getSubjectName())
                .teacherId(courseClass.getTeacher().getId())
                .teacherCode(courseClass.getTeacher().getTeacherCode())
                .teacherName(courseClass.getTeacher().getFullName())
                .studentGroupId(courseClass.getStudentGroup().getId())
                .studentGroupCode(courseClass.getStudentGroup().getGroupCode())
                .studentGroupName(courseClass.getStudentGroup().getGroupName())
                .numberOfStudents(courseClass.getNumberOfStudents())
                .periods(courseClass.getPeriods())
                .note(courseClass.getNote())
                .createdAt(courseClass.getCreatedAt())
                .build();
    }

    public TeacherSubjectResponse toTeacherSubjectResponse(TeacherSubject item) {
        return TeacherSubjectResponse.builder()
                .id(item.getId())
                .teacherId(item.getTeacher().getId())
                .teacherCode(item.getTeacher().getTeacherCode())
                .teacherName(item.getTeacher().getFullName())
                .subjectId(item.getSubject().getId())
                .subjectCode(item.getSubject().getSubjectCode())
                .subjectName(item.getSubject().getSubjectName())
                .build();
    }

    public TeacherAvailableTimeResponse toTeacherAvailableTimeResponse(TeacherAvailableTime item) {
        TimeSlot t = item.getTimeSlot();
        return TeacherAvailableTimeResponse.builder()
                .id(item.getId())
                .teacherId(item.getTeacher().getId())
                .teacherCode(item.getTeacher().getTeacherCode())
                .teacherName(item.getTeacher().getFullName())
                .timeSlotId(t.getId())
                .dayOfWeek(t.getDayOfWeek())
                .dayName(toDayName(t.getDayOfWeek()))
                .slotIndex(t.getSlotIndex())
                .startTime(formatTime(t.getStartTime()))
                .endTime(formatTime(t.getEndTime()))
                .description(t.getDescription())
                .build();
    }

    public TeacherPreferredTimeResponse toTeacherPreferredTimeResponse(TeacherPreferredTime item) {
        TimeSlot t = item.getTimeSlot();
        return TeacherPreferredTimeResponse.builder()
                .id(item.getId())
                .teacherId(item.getTeacher().getId())
                .teacherCode(item.getTeacher().getTeacherCode())
                .teacherName(item.getTeacher().getFullName())
                .timeSlotId(t.getId())
                .dayOfWeek(t.getDayOfWeek())
                .dayName(toDayName(t.getDayOfWeek()))
                .slotIndex(t.getSlotIndex())
                .startTime(formatTime(t.getStartTime()))
                .endTime(formatTime(t.getEndTime()))
                .description(t.getDescription())
                .priorityScore(item.getPriorityScore())
                .build();
    }

    public RoomAvailableTimeResponse toRoomAvailableTimeResponse(RoomAvailableTime item) {
        TimeSlot t = item.getTimeSlot();
        return RoomAvailableTimeResponse.builder()
                .id(item.getId())
                .roomId(item.getRoom().getId())
                .roomCode(item.getRoom().getRoomCode())
                .roomName(item.getRoom().getRoomName())
                .timeSlotId(t.getId())
                .dayOfWeek(t.getDayOfWeek())
                .dayName(toDayName(t.getDayOfWeek()))
                .slotIndex(t.getSlotIndex())
                .startTime(formatTime(t.getStartTime()))
                .endTime(formatTime(t.getEndTime()))
                .description(t.getDescription())
                .build();
    }

    public RoomEquipmentResponse toRoomEquipmentResponse(RoomEquipment item) {
        return RoomEquipmentResponse.builder()
                .id(item.getId())
                .roomId(item.getRoom().getId())
                .roomCode(item.getRoom().getRoomCode())
                .roomName(item.getRoom().getRoomName())
                .equipmentId(item.getEquipment().getId())
                .equipmentCode(item.getEquipment().getEquipmentCode())
                .equipmentName(item.getEquipment().getEquipmentName())
                .build();
    }

    public CourseClassRequiredEquipmentResponse toCourseClassRequiredEquipmentResponse(CourseClassRequiredEquipment item) {
        return CourseClassRequiredEquipmentResponse.builder()
                .id(item.getId())
                .courseClassId(item.getCourseClass().getId())
                .classCode(item.getCourseClass().getClassCode())
                .equipmentId(item.getEquipment().getId())
                .equipmentCode(item.getEquipment().getEquipmentCode())
                .equipmentName(item.getEquipment().getEquipmentName())
                .build();
    }

    public ScheduleRunResponse toScheduleRunResponse(ScheduleRun run) {
        return ScheduleRunResponse.builder()
                .id(run.getId())
                .runName(run.getRunName())
                .populationSize(run.getPopulationSize())
                .generations(run.getGenerations())
                .mutationRate(run.getMutationRate())
                .crossoverRate(run.getCrossoverRate())
                .totalPenalty(run.getTotalPenalty())
                .hardConstraintViolations(run.getHardConstraintViolations())
                .softConstraintViolations(run.getSoftConstraintViolations())
                .status(run.getStatus())
                .createdAt(run.getCreatedAt())
                .build();
    }

    public TimetableEntryResponse toTimetableEntryResponse(TimetableEntry entry) {
        CourseClass cc = entry.getCourseClass();
        TimeSlot t = entry.getTimeSlot();
        return TimetableEntryResponse.builder()
                .id(entry.getId())
                .scheduleRunId(entry.getScheduleRun().getId())
                .courseClassId(cc.getId())
                .classCode(cc.getClassCode())
                .subjectId(cc.getSubject().getId())
                .subjectCode(cc.getSubject().getSubjectCode())
                .subjectName(cc.getSubject().getSubjectName())
                .teacherId(entry.getTeacher().getId())
                .teacherCode(entry.getTeacher().getTeacherCode())
                .teacherName(entry.getTeacher().getFullName())
                .studentGroupId(entry.getStudentGroup().getId())
                .studentGroupCode(entry.getStudentGroup().getGroupCode())
                .studentGroupName(entry.getStudentGroup().getGroupName())
                .roomId(entry.getRoom().getId())
                .roomCode(entry.getRoom().getRoomCode())
                .roomName(entry.getRoom().getRoomName())
                .building(entry.getRoom().getBuilding())
                .timeSlotId(t.getId())
                .dayOfWeek(t.getDayOfWeek())
                .dayName(toDayName(t.getDayOfWeek()))
                .slotIndex(t.getSlotIndex())
                .startTime(formatTime(t.getStartTime()))
                .endTime(formatTime(t.getEndTime()))
                .description(t.getDescription())
                .penalty(entry.getPenalty())
                .build();
    }

    public String toDayName(Integer dayOfWeek) {
        if (dayOfWeek == null) {
            return null;
        }
        return switch (dayOfWeek) {
            case 2 -> "Thứ 2";
            case 3 -> "Thứ 3";
            case 4 -> "Thứ 4";
            case 5 -> "Thứ 5";
            case 6 -> "Thứ 6";
            case 7 -> "Thứ 7";
            default -> "Không xác định";
        };
    }

    public String formatTime(LocalTime time) {
        return time == null ? null : time.format(TIME_FORMATTER);
    }
}
