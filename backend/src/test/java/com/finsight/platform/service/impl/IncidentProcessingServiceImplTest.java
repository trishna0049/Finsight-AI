package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.entity.IncidentLog;
import com.finsight.platform.domain.entity.MonitoredService;
import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.domain.enums.IncidentStatus;
import com.finsight.platform.events.model.IncidentCreatedEvent;
import com.finsight.platform.exception.ResourceNotFoundException;
import com.finsight.platform.repository.IncidentLogRepository;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.service.AiAnalysisService;
import com.finsight.platform.service.AuditService;
import com.finsight.platform.service.DashboardService;
import com.finsight.platform.service.NotificationService;
import com.finsight.platform.service.SeverityEngineService;
import com.finsight.platform.service.impl.model.AiAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentProcessingServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentLogRepository incidentLogRepository;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Mock
    private SeverityEngineService severityEngineService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private IncidentProcessingServiceImpl incidentProcessingService;

    private Incident incident;
    private IncidentCreatedEvent event;

    @BeforeEach
    void setUp() {
        MonitoredService service = new MonitoredService();
        service.setName("payment-gateway-service");

        incident = new Incident();
        incident.setId(1L);
        incident.setIncidentKey("INC-123456");
        incident.setTitle("Payment Service Down");
        incident.setDescription("Gateway returning 500s");
        incident.setService(service);
        incident.setAffectedUsers(1500);
        incident.setResponseTimeMs(2200);
        incident.setErrorFrequency(BigDecimal.valueOf(64.5));
        incident.setStatus(IncidentStatus.OPEN);

        event = new IncidentCreatedEvent(1L, "payment-failure", OffsetDateTime.now());
    }

    @Test
    void shouldProcessIncidentAndPopulateAiFields() {
        IncidentLog log = mock(IncidentLog.class);
        when(log.getMessage()).thenReturn("Circuit open for upstream dependency");

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentLogRepository.findTop20ByIncidentIdOrderByTimestampDesc(1L))
                .thenReturn(List.of(log));
        when(aiAnalysisService.analyze(any())).thenReturn(new AiAnalysisResult(
                "Executive summary text",
                "Root cause text",
                "Business impact text",
                0.91,
                "Suggested resolution text"
        ));
        when(severityEngineService.classify(incident)).thenReturn(IncidentSeverity.HIGH);

        incidentProcessingService.processCreatedIncident(event);

        assertEquals("Executive summary text", incident.getAiSummary());
        assertEquals("Root cause text", incident.getRootCause());
        assertEquals("Business impact text", incident.getBusinessImpact());
        assertEquals("Suggested resolution text", incident.getSuggestedResolution());
        assertEquals(0, BigDecimal.valueOf(0.91).compareTo(incident.getConfidenceScore()));
        assertEquals(IncidentSeverity.HIGH, incident.getSeverity());
        assertEquals(IncidentStatus.INVESTIGATING, incident.getStatus());

        verify(incidentRepository, times(1)).save(incident);
        verify(notificationService, times(1)).notifyIncident(incident);
        verify(auditService, times(1)).record(any(), any(), any());
        verify(dashboardService, times(1)).evictSummaryCache();
    }

    @Test
    void shouldThrowWhenIncidentNotFound() {
        when(incidentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> incidentProcessingService.processCreatedIncident(event));

        verify(incidentRepository, never()).save(any());
        verify(notificationService, never()).notifyIncident(any());
        verify(auditService, never()).record(any(), any(), any());
    }

    @Test
    void shouldPassCorrectDataToAiAnalysisService() {
        IncidentLog log = mock(IncidentLog.class);
        when(log.getMessage()).thenReturn("Connection pool exhausted");

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentLogRepository.findTop20ByIncidentIdOrderByTimestampDesc(1L))
                .thenReturn(List.of(log));
        when(aiAnalysisService.analyze(any())).thenReturn(new AiAnalysisResult(
                "summary", "cause", "impact", 0.75, "resolution"
        ));
        when(severityEngineService.classify(incident)).thenReturn(IncidentSeverity.MEDIUM);

        incidentProcessingService.processCreatedIncident(event);

        verify(aiAnalysisService, times(1)).analyze(argThat(request ->
                request.title().equals("Payment Service Down")
                        && request.serviceName().equals("payment-gateway-service")
                        && request.affectedUsers() == 1500
                        && request.recentLogs().contains("Connection pool exhausted")
        ));
    }
}
