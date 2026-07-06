package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student.StudentGroupRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Student.StudentGroupResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.StudentGroup;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentGroupService {

    private final StudentGroupRepository studentGroupRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<StudentGroupResponse> getAll() {
        return studentGroupRepository.findAll().stream().map(mapper::toStudentGroupResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentGroupResponse getById(Integer id) {
        return mapper.toStudentGroupResponse(findById(id));
    }

    public StudentGroupResponse create(StudentGroupRequest request) {
        if (studentGroupRepository.existsByGroupCode(request.getGroupCode())) {
            throw new DuplicateResourceException("Mã nhóm sinh viên đã tồn tại");
        }
        StudentGroup group = StudentGroup.builder()
                .groupCode(request.getGroupCode())
                .groupName(request.getGroupName())
                .numberOfStudents(request.getNumberOfStudents())
                .build();
        return mapper.toStudentGroupResponse(studentGroupRepository.save(group));
    }

    public StudentGroupResponse update(Integer id, StudentGroupRequest request) {
        StudentGroup group = findById(id);
        studentGroupRepository.findByGroupCode(request.getGroupCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Mã nhóm sinh viên đã tồn tại"); });

        group.setGroupCode(request.getGroupCode());
        group.setGroupName(request.getGroupName());
        group.setNumberOfStudents(request.getNumberOfStudents());
        return mapper.toStudentGroupResponse(studentGroupRepository.save(group));
    }

    public void delete(Integer id) {
        studentGroupRepository.delete(findById(id));
    }

    public StudentGroup findById(Integer id) {
        return studentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm sinh viên với id: " + id));
    }
}
