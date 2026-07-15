package com.finsight.platform.service;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.entity.MonitoredService;
import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.service.impl.SeverityEngineServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SeverityEngineServiceImplTest {

    private final SeverityEngineServiceImpl service = new SeverityEngineServiceImpl();

    @Test
    void shouldClassifyCriticalWhenBusinessAndTechnicalSignalsAreHigh() {
        Incident incident = new Incident();
        MonitoredService monitoredService = new MonitoredService();
        monitoredService.setName("Payment Service");

        incident.setService(monitoredService);
        incident.setAffectedUsers(4000);
        incident.setResponseTimeMs(3000);
        incident.setErrorFrequency(100.0);

        IncidentSeverity severity = service.classify(incident);
        Assertions.assertEquals(IncidentSeverity.CRITICAL, severity);
    }

    @Test
    void shouldClassifyLowWhenSignalsAreMinimal() {
        Incident incident = new Incident();
        MonitoredService monitoredService = new MonitoredService();
        monitoredService.setName("Document Service");

        incident.setService(monitoredService);
        incident.setAffectedUsers(10);
        incident.setResponseTimeMs(80);
        incident.setErrorFrequency(1.0);

        IncidentSeverity severity = service.classify(incident);
        Assertions.assertEquals(IncidentSeverity.LOW, severity);
    }
}
