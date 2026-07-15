package com.finsight.platform.domain.entity;

import com.finsight.platform.domain.enums.LogLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "incident_logs")
public class IncidentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime timestamp = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private MonitoredService service;

    @Column(nullable = false, length = 32)
    private String environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LogLevel logLevel = LogLevel.INFO;

    @Column(nullable = false, length = 120)
    private String correlationId;

    @Column(nullable = false)
    private Integer responseTimeMs;

    @Column(length = 64)
    private String errorCode;

    @Column(length = 255)
    private String exception;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;
}
