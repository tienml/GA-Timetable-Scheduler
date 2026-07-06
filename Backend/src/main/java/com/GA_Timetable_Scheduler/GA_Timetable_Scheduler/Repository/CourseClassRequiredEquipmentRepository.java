package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.CourseClassRequiredEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseClassRequiredEquipmentRepository extends JpaRepository<CourseClassRequiredEquipment, Integer> {

    List<CourseClassRequiredEquipment> findByCourseClassId(Integer courseClassId);

    List<CourseClassRequiredEquipment> findByEquipmentId(Integer equipmentId);

    boolean existsByCourseClassIdAndEquipmentId(Integer courseClassId, Integer equipmentId);
}