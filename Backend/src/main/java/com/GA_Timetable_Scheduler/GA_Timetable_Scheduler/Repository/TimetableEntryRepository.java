package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Integer> {

    List<TimetableEntry> findByScheduleRunId(Integer scheduleRunId);

    List<TimetableEntry> findByCourseClassId(Integer courseClassId);

    List<TimetableEntry> findByTeacherId(Integer teacherId);

    List<TimetableEntry> findByStudentGroupId(Integer studentGroupId);

    List<TimetableEntry> findByRoomId(Integer roomId);

    boolean existsByScheduleRunIdAndCourseClassId(Integer scheduleRunId, Integer courseClassId);

    boolean existsByScheduleRunIdAndRoomIdAndTimeSlotId(
            Integer scheduleRunId,
            Integer roomId,
            Integer timeSlotId
    );

    boolean existsByScheduleRunIdAndTeacherIdAndTimeSlotId(
            Integer scheduleRunId,
            Integer teacherId,
            Integer timeSlotId
    );

    boolean existsByScheduleRunIdAndStudentGroupIdAndTimeSlotId(
            Integer scheduleRunId,
            Integer studentGroupId,
            Integer timeSlotId
    );
}