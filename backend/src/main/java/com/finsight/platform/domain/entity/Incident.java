package com.finsight.platform.domain.entity;

import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.domain.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String incidentKey;

    @Column(nullable = false, length = 240)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private MonitoredService service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentSeverity severity = IncidentSeverity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentStatus status = IncidentStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(nullable = false)
    private Integer affectedUsers = 0;

    @Column(nullable = false)
    private Integer responseTimeMs = 0;

    @Column(nullable = false, precision = 8, scale = 2)
   private BigDecimal errorFrequency = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String rootCause;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Column(columnDefinition = "TEXT")
    private String businessImpact;

    @Column(columnDefinition = "TEXT")
    private String suggestedResolution;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal confidenceScore = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column
    private OffsetDateTime resolvedAt;
}
