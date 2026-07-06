package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.TeacherAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherAvailableTimeRepository extends JpaRepository<TeacherAvailableTime, Integer> {

    List<TeacherAvailableTime> findByTeacherId(Integer teacherId);

    List<TeacherAvailableTime> findByTimeSlotId(Integer timeSlotId);

    boolean existsByTeacherIdAndTimeSlotId(Integer teacherId, Integer timeSlotId);
}