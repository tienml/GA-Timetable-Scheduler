package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

    Optional<StudentGroup> findByGroupCode(String groupCode);

    boolean existsByGroupCode(String groupCode);
}