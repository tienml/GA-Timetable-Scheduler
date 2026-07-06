package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    Optional<TimeSlot> findByDayOfWeekAndSlotIndex(Integer dayOfWeek, Integer slotIndex);

    List<TimeSlot> findByDayOfWeekOrderBySlotIndexAsc(Integer dayOfWeek);

    List<TimeSlot> findAllByOrderByDayOfWeekAscSlotIndexAsc();
}