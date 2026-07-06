package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass.CourseClassRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.CourseClass.CourseClassResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.CourseClass;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.StudentGroup;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Subject;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Teacher;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.CourseClassRepository;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.StudentGroupRepository;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.SubjectRepository;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseClassService {

    private final CourseClassRepository courseClassRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<CourseClassResponse> getAll() {
        return courseClassRepository.findAll().stream().map(mapper::toCourseClassResponse).toList();
    }

    @Transactional(readOnly = true)
    public CourseClassResponse getById(Integer id) {
        return mapper.toCourseClassResponse(findById(id));
    }

    public CourseClassResponse create(CourseClassRequest request) {
        if (courseClassRepository.existsByClassCode(request.getClassCode())) {
            throw new DuplicateResourceException("Mã lớp học phần đã tồn tại");
        }

        Subject subject = findSubject(request.getSubjectId());
        Teacher teacher = findTeacher(request.getTeacherId());
        StudentGroup studentGroup = findStudentGroup(request.getStudentGroupId());

        CourseClass courseClass = CourseClass.builder()
                .classCode(request.getClassCode())
                .subject(subject)
                .teacher(teacher)
                .studentGroup(studentGroup)
                .numberOfStudents(request.getNumberOfStudents())
                .periods(request.getPeriods())
                .note(request.getNote())
                .build();
        return mapper.toCourseClassResponse(courseClassRepository.save(courseClass));
    }

    public CourseClassResponse update(Integer id, CourseClassRequest request) {
        CourseClass courseClass = findById(id);
        courseClassRepository.findByClassCode(request.getClassCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Mã lớp học phần đã tồn tại"); });

        courseClass.setClassCode(request.getClassCode());
        courseClass.setSubject(findSubject(request.getSubjectId()));
        courseClass.setTeacher(findTeacher(request.getTeacherId()));
        courseClass.setStudentGroup(findStudentGroup(request.getStudentGroupId()));
        courseClass.setNumberOfStudents(request.getNumberOfStudents());
        courseClass.setPeriods(request.getPeriods());
        courseClass.setNote(request.getNote());
        return mapper.toCourseClassResponse(courseClassRepository.save(courseClass));
    }

    public void delete(Integer id) {
        courseClassRepository.delete(findById(id));
    }

    public CourseClass findById(Integer id) {
        return courseClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học phần với id: " + id));
    }

    private Subject findSubject(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với id: " + id));
    }

    private Teacher findTeacher(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giáo viên với id: " + id));
    }

    private StudentGroup findStudentGroup(Integer id) {
        return studentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm sinh viên với id: " + id));
    }
}
