package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.TeacherPreferredTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherPreferredTimeRepository extends JpaRepository<TeacherPreferredTime, Integer> {

    List<TeacherPreferredTime> findByTeacherId(Integer teacherId);

    List<TeacherPreferredTime> findByTimeSlotId(Integer timeSlotId);

    boolean existsByTeacherIdAndTimeSlotId(Integer teacherId, Integer timeSlotId);
}