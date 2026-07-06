package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Relationship.*;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.*;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RelationshipService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final CourseClassRepository courseClassRepository;

    private final TeacherSubjectRepository teacherSubjectRepository;
    private final TeacherAvailableTimeRepository teacherAvailableTimeRepository;
    private final TeacherPreferredTimeRepository teacherPreferredTimeRepository;
    private final RoomAvailableTimeRepository roomAvailableTimeRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;
    private final CourseClassRequiredEquipmentRepository courseClassRequiredEquipmentRepository;

    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<TeacherSubjectResponse> getAllTeacherSubjects() {
        return teacherSubjectRepository.findAll().stream().map(mapper::toTeacherSubjectResponse).toList();
    }

    public TeacherSubjectResponse createTeacherSubject(TeacherSubjectRequest request) {
        if (teacherSubjectRepository.existsByTeacherIdAndSubjectId(request.getTeacherId(), request.getSubjectId())) {
            throw new DuplicateResourceException("Giáo viên đã được gán môn học này");
        }
        TeacherSubject item = TeacherSubject.builder()
                .teacher(findTeacher(request.getTeacherId()))
                .subject(findSubject(request.getSubjectId()))
                .build();
        return mapper.toTeacherSubjectResponse(teacherSubjectRepository.save(item));
    }

    public void deleteTeacherSubject(Integer id) {
        TeacherSubject item = teacherSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dữ liệu giáo viên - môn học với id: " + id));
        teacherSubjectRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<TeacherAvailableTimeResponse> getAllTeacherAvailableTimes() {
        return teacherAvailableTimeRepository.findAll().stream().map(mapper::toTeacherAvailableTimeResponse).toList();
    }

    public TeacherAvailableTimeResponse createTeacherAvailableTime(TeacherAvailableTimeRequest request) {
        if (teacherAvailableTimeRepository.existsByTeacherIdAndTimeSlotId(request.getTeacherId(), request.getTimeSlotId())) {
            throw new DuplicateResourceException("Thời gian rảnh của giáo viên đã tồn tại");
        }
        TeacherAvailableTime item = TeacherAvailableTime.builder()
                .teacher(findTeacher(request.getTeacherId()))
                .timeSlot(findTimeSlot(request.getTimeSlotId()))
                .build();
        return mapper.toTeacherAvailableTimeResponse(teacherAvailableTimeRepository.save(item));
    }

    public void deleteTeacherAvailableTime(Integer id) {
        TeacherAvailableTime item = teacherAvailableTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thời gian rảnh của giáo viên với id: " + id));
        teacherAvailableTimeRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<TeacherPreferredTimeResponse> getAllTeacherPreferredTimes() {
        return teacherPreferredTimeRepository.findAll().stream().map(mapper::toTeacherPreferredTimeResponse).toList();
    }

    public TeacherPreferredTimeResponse createTeacherPreferredTime(TeacherPreferredTimeRequest request) {
        if (teacherPreferredTimeRepository.existsByTeacherIdAndTimeSlotId(request.getTeacherId(), request.getTimeSlotId())) {
            throw new DuplicateResourceException("Thời gian mong muốn của giáo viên đã tồn tại");
        }
        TeacherPreferredTime item = TeacherPreferredTime.builder()
                .teacher(findTeacher(request.getTeacherId()))
                .timeSlot(findTimeSlot(request.getTimeSlotId()))
                .priorityScore(request.getPriorityScore())
                .build();
        return mapper.toTeacherPreferredTimeResponse(teacherPreferredTimeRepository.save(item));
    }

    public void deleteTeacherPreferredTime(Integer id) {
        TeacherPreferredTime item = teacherPreferredTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thời gian mong muốn của giáo viên với id: " + id));
        teacherPreferredTimeRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<RoomAvailableTimeResponse> getAllRoomAvailableTimes() {
        return roomAvailableTimeRepository.findAll().stream().map(mapper::toRoomAvailableTimeResponse).toList();
    }

    public RoomAvailableTimeResponse createRoomAvailableTime(RoomAvailableTimeRequest request) {
        if (roomAvailableTimeRepository.existsByRoomIdAndTimeSlotId(request.getRoomId(), request.getTimeSlotId())) {
            throw new DuplicateResourceException("Thời gian trống của phòng đã tồn tại");
        }
        RoomAvailableTime item = RoomAvailableTime.builder()
                .room(findRoom(request.getRoomId()))
                .timeSlot(findTimeSlot(request.getTimeSlotId()))
                .build();
        return mapper.toRoomAvailableTimeResponse(roomAvailableTimeRepository.save(item));
    }

    public void deleteRoomAvailableTime(Integer id) {
        RoomAvailableTime item = roomAvailableTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thời gian trống của phòng với id: " + id));
        roomAvailableTimeRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<RoomEquipmentResponse> getAllRoomEquipment() {
        return roomEquipmentRepository.findAll().stream().map(mapper::toRoomEquipmentResponse).toList();
    }

    public RoomEquipmentResponse createRoomEquipment(RoomEquipmentRequest request) {
        if (roomEquipmentRepository.existsByRoomIdAndEquipmentId(request.getRoomId(), request.getEquipmentId())) {
            throw new DuplicateResourceException("Thiết bị đã được gán cho phòng này");
        }
        RoomEquipment item = RoomEquipment.builder()
                .room(findRoom(request.getRoomId()))
                .equipment(findEquipment(request.getEquipmentId()))
                .build();
        return mapper.toRoomEquipmentResponse(roomEquipmentRepository.save(item));
    }

    public void deleteRoomEquipment(Integer id) {
        RoomEquipment item = roomEquipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dữ liệu phòng - thiết bị với id: " + id));
        roomEquipmentRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<CourseClassRequiredEquipmentResponse> getAllCourseClassRequiredEquipment() {
        return courseClassRequiredEquipmentRepository.findAll().stream().map(mapper::toCourseClassRequiredEquipmentResponse).toList();
    }

    public CourseClassRequiredEquipmentResponse createCourseClassRequiredEquipment(CourseClassRequiredEquipmentRequest request) {
        if (courseClassRequiredEquipmentRepository.existsByCourseClassIdAndEquipmentId(request.getCourseClassId(), request.getEquipmentId())) {
            throw new DuplicateResourceException("Thiết bị yêu cầu đã được gán cho lớp học phần này");
        }
        CourseClassRequiredEquipment item = CourseClassRequiredEquipment.builder()
                .courseClass(findCourseClass(request.getCourseClassId()))
                .equipment(findEquipment(request.getEquipmentId()))
                .build();
        return mapper.toCourseClassRequiredEquipmentResponse(courseClassRequiredEquipmentRepository.save(item));
    }

    public void deleteCourseClassRequiredEquipment(Integer id) {
        CourseClassRequiredEquipment item = courseClassRequiredEquipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị yêu cầu của lớp học phần với id: " + id));
        courseClassRequiredEquipmentRepository.delete(item);
    }

    private Teacher findTeacher(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giáo viên với id: " + id));
    }

    private Subject findSubject(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với id: " + id));
    }

    private TimeSlot findTimeSlot(Integer id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ với id: " + id));
    }

    private Room findRoom(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng học với id: " + id));
    }

    private Equipment findEquipment(Integer id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị với id: " + id));
    }

    private CourseClass findCourseClass(Integer id) {
        return courseClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học phần với id: " + id));
    }
}
