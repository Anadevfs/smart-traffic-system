package com.smarttraffic.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TrafficHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "intersection_id")
    private Intersection intersection;

    private int carCount;
    private String condition;

    // salva a decisão da inteligência
    private int suggestedGreenTime;

    private LocalDateTime timestamp;

    public TrafficHistory() {}

    // decisão do tempo
    public TrafficHistory(Intersection intersection, int carCount, String condition, int suggestedGreenTime) {
        this.intersection = intersection;
        this.carCount = carCount;
        this.condition = condition;
        this.suggestedGreenTime = suggestedGreenTime;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Intersection getIntersection() { return intersection; }
    public int getCarCount() { return carCount; }
    public String getCondition() { return condition; }
    public int getSuggestedGreenTime() { return suggestedGreenTime; }
    public LocalDateTime getTimestamp() { return timestamp; }
}