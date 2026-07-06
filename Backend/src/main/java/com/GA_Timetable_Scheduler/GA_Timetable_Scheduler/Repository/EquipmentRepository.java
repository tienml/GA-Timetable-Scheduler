package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

    Optional<Equipment> findByEquipmentCode(String equipmentCode);

    boolean existsByEquipmentCode(String equipmentCode);
}