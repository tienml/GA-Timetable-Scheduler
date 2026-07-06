package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment.EquipmentRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.Equipment.EquipmentResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.Equipment;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.DuplicateResourceException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final TimetableMapper mapper;

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAll() {
        return equipmentRepository.findAll().stream().map(mapper::toEquipmentResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getById(Integer id) {
        return mapper.toEquipmentResponse(findById(id));
    }

    public EquipmentResponse create(EquipmentRequest request) {
        if (equipmentRepository.existsByEquipmentCode(request.getEquipmentCode())) {
            throw new DuplicateResourceException("Mã thiết bị đã tồn tại");
        }
        Equipment equipment = Equipment.builder()
                .equipmentCode(request.getEquipmentCode())
                .equipmentName(request.getEquipmentName())
                .build();
        return mapper.toEquipmentResponse(equipmentRepository.save(equipment));
    }

    public EquipmentResponse update(Integer id, EquipmentRequest request) {
        Equipment equipment = findById(id);
        equipmentRepository.findByEquipmentCode(request.getEquipmentCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Mã thiết bị đã tồn tại"); });

        equipment.setEquipmentCode(request.getEquipmentCode());
        equipment.setEquipmentName(request.getEquipmentName());
        return mapper.toEquipmentResponse(equipmentRepository.save(equipment));
    }

    public void delete(Integer id) {
        equipmentRepository.delete(findById(id));
    }

    public Equipment findById(Integer id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị với id: " + id));
    }
}
