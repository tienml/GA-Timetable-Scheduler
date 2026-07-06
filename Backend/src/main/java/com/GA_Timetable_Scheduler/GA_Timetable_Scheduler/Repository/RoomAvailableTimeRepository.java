package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.RoomAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomAvailableTimeRepository extends JpaRepository<RoomAvailableTime, Integer> {

    List<RoomAvailableTime> findByRoomId(Integer roomId);

    List<RoomAvailableTime> findByTimeSlotId(Integer timeSlotId);

    boolean existsByRoomIdAndTimeSlotId(Integer roomId, Integer timeSlotId);
}