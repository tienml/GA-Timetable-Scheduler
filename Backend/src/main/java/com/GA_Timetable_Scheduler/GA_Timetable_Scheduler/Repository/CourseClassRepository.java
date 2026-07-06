package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.CourseClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseClassRepository extends JpaRepository<CourseClass, Integer> {

    Optional<CourseClass> findByClassCode(String classCode);

    boolean existsByClassCode(String classCode);

    List<CourseClass> findByTeacherId(Integer teacherId);

    List<CourseClass> findBySubjectId(Integer subjectId);

    List<CourseClass> findByStudentGroupId(Integer studentGroupId);
}