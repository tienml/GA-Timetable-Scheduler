package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher.TeacherRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Teacher.TeacherResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Teacher;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<TeacherResponse> getAll() {
        return teacherRepository.findAll().stream().map(mapper::toTeacherResponse).toList();
    }

    @Transactional(readOnly = true)
    public TeacherResponse getById(Integer id) {
        return mapper.toTeacherResponse(findById(id));
    }

    public TeacherResponse create(TeacherRequest request) {
        if (teacherRepository.existsByTeacherCode(request.getTeacherCode())) {
            throw new DuplicateResourceException("Mã giáo viên đã tồn tại");
        }
        Teacher teacher = Teacher.builder()
                .teacherCode(request.getTeacherCode())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        return mapper.toTeacherResponse(teacherRepository.save(teacher));
    }

    public TeacherResponse update(Integer id, TeacherRequest request) {
        Teacher teacher = findById(id);
        teacherRepository.findByTeacherCode(request.getTeacherCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Mã giáo viên đã tồn tại"); });

        teacher.setTeacherCode(request.getTeacherCode());
        teacher.setFullName(request.getFullName());
        teacher.setEmail(request.getEmail());
        teacher.setPhone(request.getPhone());
        return mapper.toTeacherResponse(teacherRepository.save(teacher));
    }

    public void delete(Integer id) {
        teacherRepository.delete(findById(id));
    }

    public Teacher findById(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giáo viên với id: " + id));
    }
}
