package com.GA_Timetable_Scheduler.GA_Timetable_Scheduler.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "schedule_runs")
public class ScheduleRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "run_name", length = 100)
    private String runName;

    @Column(name = "population_size", nullable = false)
    private Integer populationSize;

    @Column(name = "generations", nullable = false)
    private Integer generations;

    @Column(name = "mutation_rate", nullable = false)
    private Double mutationRate;

    @Column(name = "crossover_rate", nullable = false)
    private Double crossoverRate;

    @Column(name = "total_penalty")
    private Integer totalPenalty = 0;

    @Column(name = "hard_constraint_violations")
    private Integer hardConstraintViolations = 0;

    @Column(name = "soft_constraint_violations")
    private Integer softConstraintViolations = 0;

    @Column(name = "status", length = 30)
    private String status = "COMPLETED";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}