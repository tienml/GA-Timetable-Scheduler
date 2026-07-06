package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room.RoomRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Room.RoomResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Room;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<RoomResponse> getAll() {
        return roomRepository.findAll().stream().map(mapper::toRoomResponse).toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse getById(Integer id) {
        return mapper.toRoomResponse(findById(id));
    }

    public RoomResponse create(RoomRequest request) {
        if (roomRepository.existsByRoomCode(request.getRoomCode())) {
            throw new DuplicateResourceException("Mã phòng học đã tồn tại");
        }
        Room room = Room.builder()
                .roomCode(request.getRoomCode())
                .roomName(request.getRoomName())
                .capacity(request.getCapacity())
                .building(request.getBuilding())
                .floorNumber(request.getFloorNumber())
                .build();
        return mapper.toRoomResponse(roomRepository.save(room));
    }

    public RoomResponse update(Integer id, RoomRequest request) {
        Room room = findById(id);
        roomRepository.findByRoomCode(request.getRoomCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Mã phòng học đã tồn tại"); });

        room.setRoomCode(request.getRoomCode());
        room.setRoomName(request.getRoomName());
        room.setCapacity(request.getCapacity());
        room.setBuilding(request.getBuilding());
        room.setFloorNumber(request.getFloorNumber());
        return mapper.toRoomResponse(roomRepository.save(room));
    }

    public void delete(Integer id) {
        roomRepository.delete(findById(id));
    }

    public Room findById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng học với id: " + id));
    }
}
