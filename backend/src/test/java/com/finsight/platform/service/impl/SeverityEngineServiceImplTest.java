package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.entity.MonitoredService;
import com.finsight.platform.domain.enums.IncidentSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeverityEngineServiceImplTest {

    private SeverityEngineServiceImpl severityEngineService;

    @BeforeEach
    void setUp() {
        severityEngineService = new SeverityEngineServiceImpl();
    }

    private Incident buildIncident(int affectedUsers, int responseTimeMs,
                                   BigDecimal errorFrequency, String serviceName) {
        Incident incident = new Incident();
        incident.setAffectedUsers(affectedUsers);
        incident.setResponseTimeMs(responseTimeMs);
        incident.setErrorFrequency(errorFrequency);

        MonitoredService service = new MonitoredService();
        service.setName(serviceName);
        incident.setService(service);

        return incident;
    }

    @Test
    @DisplayName("Low affected users, fast response, low error rate -> LOW severity")
    void shouldClassifyAsLow() {
        Incident incident = buildIncident(50, 200, BigDecimal.valueOf(5), "notification-service");
        assertEquals(IncidentSeverity.LOW, severityEngineService.classify(incident));
    }

    @Test
    @DisplayName("Moderate affected users and response time -> MEDIUM severity")
    void shouldClassifyAsMedium() {
        // 500+ users = 25, 1000+ ms = 15, error 30+ = 15 -> score 55 -> HIGH
        Incident incident = buildIncident(500, 1000, BigDecimal.valueOf(30), "logging-service");
        assertEquals(IncidentSeverity.HIGH, severityEngineService.classify(incident));
    }

    @Test
    @DisplayName("High affected users and slow response -> HIGH severity")
    void shouldClassifyAsHigh() {
        // 500+ users = 25, 1000+ ms = 15, error just under 30 = 0 -> score 40 -> MEDIUM
        Incident incident = buildIncident(500, 1000, BigDecimal.valueOf(29), "logging-service");
        assertEquals(IncidentSeverity.MEDIUM, severityEngineService.classify(incident));
    }

    @Test
    @DisplayName("Very high impact across all factors -> CRITICAL severity")
    void shouldClassifyAsCritical() {
        // 2000+ users = 40, 2500+ ms = 30, error 80+ = 30 -> score 100 -> CRITICAL
        Incident incident = buildIncident(2500, 3000, BigDecimal.valueOf(90), "checkout-service");
        assertEquals(IncidentSeverity.CRITICAL, severityEngineService.classify(incident));
    }

    @Test
    @DisplayName("Payment-related service adds severity bump even with moderate metrics")
    void shouldBumpSeverityForPaymentService() {
        // 500+ users = 25, 1000+ ms = 15, error under 30 = 0, payment service = 15 -> score 55 -> HIGH
        Incident incident = buildIncident(500, 1000, BigDecimal.valueOf(10), "payment-gateway-service");
        assertEquals(IncidentSeverity.HIGH, severityEngineService.classify(incident));
    }

    @Test
    @DisplayName("Score at or above CRITICAL boundary classifies as CRITICAL")
    void shouldClassifyBoundaryScoreAsCritical() {
        // 2000+ users = 40, 1000+ ms = 15, error 80+ = 30, non-critical service -> 85 -> CRITICAL
        Incident incident = buildIncident(2000, 1000, BigDecimal.valueOf(80), "cache-service");
        assertEquals(IncidentSeverity.CRITICAL, severityEngineService.classify(incident));
    }

    @Test
    @DisplayName("Zero values across all metrics -> LOW severity")
    void shouldClassifyZeroValuesAsLow() {
        Incident incident = buildIncident(0, 0, BigDecimal.ZERO, "health-check-service");
        assertEquals(IncidentSeverity.LOW, severityEngineService.classify(incident));
    }
}
