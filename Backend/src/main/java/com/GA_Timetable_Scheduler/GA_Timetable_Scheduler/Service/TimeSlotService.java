package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot.TimeSlotRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.TimeSlot.TimeSlotResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.TimeSlot;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.BadRequestException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAll() {
        return timeSlotRepository.findAllByOrderByDayOfWeekAscSlotIndexAsc()
                .stream()
                .map(mapper::toTimeSlotResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TimeSlotResponse getById(Integer id) {
        return mapper.toTimeSlotResponse(findById(id));
    }

    public TimeSlotResponse create(TimeSlotRequest request) {
        validateTimeRange(request);
        if (timeSlotRepository.findByDayOfWeekAndSlotIndex(request.getDayOfWeek(), request.getSlotIndex()).isPresent()) {
            throw new DuplicateResourceException("Khung giờ này đã tồn tại");
        }
        TimeSlot timeSlot = TimeSlot.builder()
                .dayOfWeek(request.getDayOfWeek())
                .slotIndex(request.getSlotIndex())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .build();
        return mapper.toTimeSlotResponse(timeSlotRepository.save(timeSlot));
    }

    public TimeSlotResponse update(Integer id, TimeSlotRequest request) {
        validateTimeRange(request);
        TimeSlot timeSlot = findById(id);
        timeSlotRepository.findByDayOfWeekAndSlotIndex(request.getDayOfWeek(), request.getSlotIndex())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Khung giờ này đã tồn tại"); });

        timeSlot.setDayOfWeek(request.getDayOfWeek());
        timeSlot.setSlotIndex(request.getSlotIndex());
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setDescription(request.getDescription());
        return mapper.toTimeSlotResponse(timeSlotRepository.save(timeSlot));
    }

    public void delete(Integer id) {
        timeSlotRepository.delete(findById(id));
    }

    public TimeSlot findById(Integer id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ với id: " + id));
    }

    private void validateTimeRange(TimeSlotRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
        }
    }
}
