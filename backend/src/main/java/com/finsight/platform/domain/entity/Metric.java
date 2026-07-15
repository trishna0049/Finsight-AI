package com.finsight.platform.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "metrics")
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String metricName;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal metricValue;

    @Column(nullable = false)
    private LocalDate metricDate;
}
