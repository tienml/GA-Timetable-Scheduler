package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Service;

import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.GenerateTimetableRequest;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.GenerateTimetableResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.ScheduleRunResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.DTO.GenerateTimetable.TimetableEntryResponse;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity.*;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.BadRequestException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Exception.ResourceNotFoundException;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Mapper.TimetableMapper;
import com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Repository.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {

    private static final int HARD_CONSTRAINT_PENALTY = 1000;
    private static final int SOFT_CONSTRAINT_PENALTY = 10;
    private static final int DEFAULT_ELITISM = 5;
    private static final int TOURNAMENT_SIZE = 3;

    private final CourseClassRepository courseClassRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final TeacherAvailableTimeRepository teacherAvailableTimeRepository;
    private final TeacherPreferredTimeRepository teacherPreferredTimeRepository;
    private final RoomAvailableTimeRepository roomAvailableTimeRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;
    private final CourseClassRequiredEquipmentRepository courseClassRequiredEquipmentRepository;
    private final ScheduleRunRepository scheduleRunRepository;
    private final TimetableEntryRepository timetableEntryRepository;
    private final TimetableMapper mapper;

    private final Random random = new Random();

    public GenerateTimetableResponse generate(GenerateTimetableRequest request) {
        List<CourseClass> courseClasses = courseClassRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<TimeSlot> timeSlots = timeSlotRepository.findAllByOrderByDayOfWeekAscSlotIndexAsc();

        validateInputData(courseClasses, rooms, timeSlots);

        ConstraintData constraintData = buildConstraintData();
        List<Chromosome> population = initializePopulation(request.getPopulationSize(), courseClasses, rooms, timeSlots);
        population.forEach(chromosome -> evaluate(chromosome, constraintData));

        for (int generation = 0; generation < request.getGenerations(); generation++) {
            population = evolve(population, request, courseClasses, rooms, timeSlots, constraintData);
        }

        Chromosome best = population.stream()
                .min(Comparator.comparingInt(Chromosome::getTotalPenalty))
                .orElseThrow(() -> new BadRequestException("Không thể tạo quần thể thời khóa biểu"));

        if (best.getHardViolations() > 0) {
            throw new BadRequestException("Không tìm được thời khóa biểu hợp lệ. Hãy bổ sung thêm phòng, khung giờ hoặc thời gian rảnh của giáo viên/phòng học.");
        }

        ScheduleRun scheduleRun = ScheduleRun.builder()
                .runName(request.getRunName() == null || request.getRunName().isBlank() ? "Lần chạy GA" : request.getRunName())
                .populationSize(request.getPopulationSize())
                .generations(request.getGenerations())
                .mutationRate(request.getMutationRate())
                .crossoverRate(request.getCrossoverRate())
                .totalPenalty(best.getTotalPenalty())
                .hardConstraintViolations(best.getHardViolations())
                .softConstraintViolations(best.getSoftViolations())
                .status("COMPLETED")
                .build();
        ScheduleRun savedRun = scheduleRunRepository.save(scheduleRun);

        List<TimetableEntry> entries = best.getGenes().stream()
                .map(gene -> TimetableEntry.builder()
                        .scheduleRun(savedRun)
                        .courseClass(gene.getCourseClass())
                        .teacher(gene.getCourseClass().getTeacher())
                        .studentGroup(gene.getCourseClass().getStudentGroup())
                        .room(gene.getRoom())
                        .timeSlot(gene.getTimeSlot())
                        .penalty(calculateGenePenalty(gene, constraintData))
                        .build())
                .toList();

        List<TimetableEntry> savedEntries = timetableEntryRepository.saveAll(entries);

        return GenerateTimetableResponse.builder()
                .scheduleRun(mapper.toScheduleRunResponse(savedRun))
                .timetableEntries(savedEntries.stream().map(mapper::toTimetableEntryResponse).toList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ScheduleRunResponse> getAllRuns() {
        return scheduleRunRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ScheduleRun::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(mapper::toScheduleRunResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GenerateTimetableResponse getLatest() {
        ScheduleRun run = scheduleRunRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Chưa có thời khóa biểu nào được tạo"));
        return getByRunId(run.getId());
    }

    @Transactional(readOnly = true)
    public GenerateTimetableResponse getByRunId(Integer runId) {
        ScheduleRun run = scheduleRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lần chạy với id: " + runId));
        List<TimetableEntryResponse> entries = timetableEntryRepository.findByScheduleRunId(runId)
                .stream()
                .sorted(Comparator
                        .comparing((TimetableEntry e) -> e.getTimeSlot().getDayOfWeek())
                        .thenComparing(e -> e.getTimeSlot().getSlotIndex())
                        .thenComparing(e -> e.getRoom().getRoomCode()))
                .map(mapper::toTimetableEntryResponse)
                .toList();
        return GenerateTimetableResponse.builder()
                .scheduleRun(mapper.toScheduleRunResponse(run))
                .timetableEntries(entries)
                .build();
    }

    private void validateInputData(List<CourseClass> courseClasses, List<Room> rooms, List<TimeSlot> timeSlots) {
        if (courseClasses.isEmpty()) {
            throw new BadRequestException("Chưa có lớp học phần để xếp lịch");
        }
        if (rooms.isEmpty()) {
            throw new BadRequestException("Chưa có phòng học để xếp lịch");
        }
        if (timeSlots.isEmpty()) {
            throw new BadRequestException("Chưa có khung giờ để xếp lịch");
        }
    }

    private List<Chromosome> initializePopulation(int size, List<CourseClass> courseClasses, List<Room> rooms, List<TimeSlot> timeSlots) {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Gene> genes = courseClasses.stream()
                    .map(courseClass -> new Gene(
                            courseClass,
                            rooms.get(random.nextInt(rooms.size())),
                            timeSlots.get(random.nextInt(timeSlots.size()))
                    ))
                    .collect(Collectors.toCollection(ArrayList::new));
            population.add(new Chromosome(genes));
        }
        return population;
    }

    private List<Chromosome> evolve(
            List<Chromosome> population,
            GenerateTimetableRequest request,
            List<CourseClass> courseClasses,
            List<Room> rooms,
            List<TimeSlot> timeSlots,
            ConstraintData constraintData
    ) {
        population.sort(Comparator.comparingInt(Chromosome::getTotalPenalty));
        List<Chromosome> nextPopulation = new ArrayList<>();

        int elitism = Math.min(DEFAULT_ELITISM, population.size());
        for (int i = 0; i < elitism; i++) {
            nextPopulation.add(population.get(i).copy());
        }

        while (nextPopulation.size() < population.size()) {
            Chromosome parent1 = tournamentSelection(population);
            Chromosome parent2 = tournamentSelection(population);

            Chromosome child;
            if (random.nextDouble() < request.getCrossoverRate()) {
                child = crossover(parent1, parent2);
            } else {
                child = parent1.copy();
            }

            mutate(child, rooms, timeSlots, request.getMutationRate());
            evaluate(child, constraintData);
            nextPopulation.add(child);
        }

        return nextPopulation;
    }

    private Chromosome tournamentSelection(List<Chromosome> population) {
        Chromosome best = null;
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            Chromosome candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.getTotalPenalty() < best.getTotalPenalty()) {
                best = candidate;
            }
        }
        return best;
    }

    private Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        int size = parent1.getGenes().size();
        if (size <= 1) {
            return parent1.copy();
        }
        int point = 1 + random.nextInt(size - 1);
        List<Gene> childGenes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Gene source = i < point ? parent1.getGenes().get(i) : parent2.getGenes().get(i);
            childGenes.add(source.copy());
        }
        return new Chromosome(childGenes);
    }

    private void mutate(Chromosome chromosome, List<Room> rooms, List<TimeSlot> timeSlots, double mutationRate) {
        for (Gene gene : chromosome.getGenes()) {
            if (random.nextDouble() < mutationRate) {
                gene.setRoom(rooms.get(random.nextInt(rooms.size())));
            }
            if (random.nextDouble() < mutationRate) {
                gene.setTimeSlot(timeSlots.get(random.nextInt(timeSlots.size())));
            }
        }
    }

    private void evaluate(Chromosome chromosome, ConstraintData data) {
        int hardViolations = 0;
        int softViolations = 0;
        int softPenalty = 0;

        Map<String, Integer> roomTimeCount = new HashMap<>();
        Map<String, Integer> teacherTimeCount = new HashMap<>();
        Map<String, Integer> groupTimeCount = new HashMap<>();

        for (Gene gene : chromosome.getGenes()) {
            CourseClass courseClass = gene.getCourseClass();
            Room room = gene.getRoom();
            TimeSlot timeSlot = gene.getTimeSlot();

            roomTimeCount.merge(key(room.getId(), timeSlot.getId()), 1, Integer::sum);
            teacherTimeCount.merge(key(courseClass.getTeacher().getId(), timeSlot.getId()), 1, Integer::sum);
            groupTimeCount.merge(key(courseClass.getStudentGroup().getId(), timeSlot.getId()), 1, Integer::sum);

            if (!data.teacherAvailableKeys.contains(key(courseClass.getTeacher().getId(), timeSlot.getId()))) {
                hardViolations++;
            }
            if (!data.roomAvailableKeys.contains(key(room.getId(), timeSlot.getId()))) {
                hardViolations++;
            }
            if (room.getCapacity() < courseClass.getNumberOfStudents()) {
                hardViolations++;
            }
            if (!roomHasRequiredEquipment(room.getId(), courseClass.getId(), data)) {
                hardViolations++;
            }
            if (!data.teacherPreferredKeys.contains(key(courseClass.getTeacher().getId(), timeSlot.getId()))) {
                softViolations++;
                softPenalty += SOFT_CONSTRAINT_PENALTY;
            }
            if (room.getCapacity() > courseClass.getNumberOfStudents() * 2) {
                softViolations++;
                softPenalty += 3;
            }
        }

        hardViolations += countConflictViolations(roomTimeCount);
        hardViolations += countConflictViolations(teacherTimeCount);
        hardViolations += countConflictViolations(groupTimeCount);

        chromosome.setHardViolations(hardViolations);
        chromosome.setSoftViolations(softViolations);
        chromosome.setTotalPenalty(hardViolations * HARD_CONSTRAINT_PENALTY + softPenalty);
    }

    private int calculateGenePenalty(Gene gene, ConstraintData data) {
        int penalty = 0;
        CourseClass courseClass = gene.getCourseClass();
        Room room = gene.getRoom();
        TimeSlot timeSlot = gene.getTimeSlot();

        if (!data.teacherAvailableKeys.contains(key(courseClass.getTeacher().getId(), timeSlot.getId()))) {
            penalty += HARD_CONSTRAINT_PENALTY;
        }
        if (!data.roomAvailableKeys.contains(key(room.getId(), timeSlot.getId()))) {
            penalty += HARD_CONSTRAINT_PENALTY;
        }
        if (room.getCapacity() < courseClass.getNumberOfStudents()) {
            penalty += HARD_CONSTRAINT_PENALTY;
        }
        if (!roomHasRequiredEquipment(room.getId(), courseClass.getId(), data)) {
            penalty += HARD_CONSTRAINT_PENALTY;
        }
        if (!data.teacherPreferredKeys.contains(key(courseClass.getTeacher().getId(), timeSlot.getId()))) {
            penalty += SOFT_CONSTRAINT_PENALTY;
        }
        if (room.getCapacity() > courseClass.getNumberOfStudents() * 2) {
            penalty += 3;
        }
        return penalty;
    }

    private int countConflictViolations(Map<String, Integer> countMap) {
        return countMap.values().stream()
                .filter(count -> count > 1)
                .mapToInt(count -> count - 1)
                .sum();
    }

    private boolean roomHasRequiredEquipment(Integer roomId, Integer courseClassId, ConstraintData data) {
        Set<Integer> required = data.requiredEquipmentByClass.getOrDefault(courseClassId, Set.of());
        if (required.isEmpty()) {
            return true;
        }
        Set<Integer> roomEquipment = data.roomEquipmentByRoom.getOrDefault(roomId, Set.of());
        return roomEquipment.containsAll(required);
    }

    private ConstraintData buildConstraintData() {
        Set<String> teacherAvailableKeys = teacherAvailableTimeRepository.findAll()
                .stream()
                .map(item -> key(item.getTeacher().getId(), item.getTimeSlot().getId()))
                .collect(Collectors.toSet());

        Set<String> teacherPreferredKeys = teacherPreferredTimeRepository.findAll()
                .stream()
                .map(item -> key(item.getTeacher().getId(), item.getTimeSlot().getId()))
                .collect(Collectors.toSet());

        Set<String> roomAvailableKeys = roomAvailableTimeRepository.findAll()
                .stream()
                .map(item -> key(item.getRoom().getId(), item.getTimeSlot().getId()))
                .collect(Collectors.toSet());

        Map<Integer, Set<Integer>> roomEquipmentByRoom = roomEquipmentRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRoom().getId(),
                        Collectors.mapping(item -> item.getEquipment().getId(), Collectors.toSet())
                ));

        Map<Integer, Set<Integer>> requiredEquipmentByClass = courseClassRequiredEquipmentRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCourseClass().getId(),
                        Collectors.mapping(item -> item.getEquipment().getId(), Collectors.toSet())
                ));

        return new ConstraintData(
                teacherAvailableKeys,
                teacherPreferredKeys,
                roomAvailableKeys,
                roomEquipmentByRoom,
                requiredEquipmentByClass
        );
    }

    private String key(Integer firstId, Integer secondId) {
        return firstId + "-" + secondId;
    }

    @Getter
    @AllArgsConstructor
    private static class ConstraintData {
        private Set<String> teacherAvailableKeys;
        private Set<String> teacherPreferredKeys;
        private Set<String> roomAvailableKeys;
        private Map<Integer, Set<Integer>> roomEquipmentByRoom;
        private Map<Integer, Set<Integer>> requiredEquipmentByClass;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Gene {
        private CourseClass courseClass;
        private Room room;
        private TimeSlot timeSlot;

        public Gene copy() {
            return new Gene(courseClass, room, timeSlot);
        }
    }

    @Getter
    @Setter
    private static class Chromosome {
        private List<Gene> genes;
        private int totalPenalty;
        private int hardViolations;
        private int softViolations;

        public Chromosome(List<Gene> genes) {
            this.genes = genes;
        }

        public Chromosome copy() {
            Chromosome copy = new Chromosome(genes.stream().map(Gene::copy).collect(Collectors.toCollection(ArrayList::new)));
            copy.setTotalPenalty(totalPenalty);
            copy.setHardViolations(hardViolations);
            copy.setSoftViolations(softViolations);
            return copy;
        }
    }
}
