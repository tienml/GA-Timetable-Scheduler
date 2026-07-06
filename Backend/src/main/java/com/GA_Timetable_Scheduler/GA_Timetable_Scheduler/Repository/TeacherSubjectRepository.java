package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Integer> {

    List<TeacherSubject> findByTeacherId(Integer teacherId);

    List<TeacherSubject> findBySubjectId(Integer subjectId);

    boolean existsByTeacherIdAndSubjectId(Integer teacherId, Integer subjectId);
}