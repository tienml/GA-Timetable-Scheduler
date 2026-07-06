package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject.SubjectRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Subject.SubjectResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Subject;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll().stream().map(mapper::toSubjectResponse).toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse getById(Integer id) {
        return mapper.toSubjectResponse(findById(id));
    }

    public SubjectResponse create(SubjectRequest request) {
        if (subjectRepository.existsBySubjectCode(request.getSubjectCode())) {
            throw new DuplicateResourceException("Mã môn học đã tồn tại");
        }
        Subject subject = Subject.builder()
                .subjectCode(request.getSubjectCode())
                .subjectName(request.getSubjectName())
                .defaultPeriods(request.getDefaultPeriods())
                .build();
        return mapper.toSubjectResponse(subjectRepository.save(subject));
    }

    public SubjectResponse update(Integer id, SubjectRequest request) {
        Subject subject = findById(id);
        subjectRepository.findBySubjectCode(request.getSubjectCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Mã môn học đã tồn tại"); });

        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setDefaultPeriods(request.getDefaultPeriods());
        return mapper.toSubjectResponse(subjectRepository.save(subject));
    }

    public void delete(Integer id) {
        subjectRepository.delete(findById(id));
    }

    public Subject findById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học với id: " + id));
    }
}
