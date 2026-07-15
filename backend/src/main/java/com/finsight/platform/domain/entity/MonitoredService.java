package com.finsight.platform.domain.entity;

import com.finsight.platform.domain.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "services")
public class MonitoredService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 160)
    private String name;

    @Column(nullable = false, length = 32)
    private String environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ServiceStatus status = ServiceStatus.HEALTHY;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal latencyMs = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cpuUsage = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal memoryUsage = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal requestsPerSec = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal availabilityPct = BigDecimal.valueOf(99.99);

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
