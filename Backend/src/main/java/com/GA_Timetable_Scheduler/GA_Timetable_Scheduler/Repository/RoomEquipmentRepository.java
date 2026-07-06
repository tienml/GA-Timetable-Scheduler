package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.RoomEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, Integer> {

    List<RoomEquipment> findByRoomId(Integer roomId);

    List<RoomEquipment> findByEquipmentId(Integer equipmentId);

    boolean existsByRoomIdAndEquipmentId(Integer roomId, Integer equipmentId);
}