package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.enums.AuditAction;
import com.finsight.platform.domain.enums.IncidentStatus;
import com.finsight.platform.events.model.IncidentCreatedEvent;
import com.finsight.platform.exception.ResourceNotFoundException;
import com.finsight.platform.repository.IncidentLogRepository;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.service.*;
import com.finsight.platform.service.impl.model.AiAnalysisRequest;
import com.finsight.platform.service.impl.model.AiAnalysisResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class IncidentProcessingServiceImpl implements IncidentProcessingService {

    private final IncidentRepository incidentRepository;
    private final IncidentLogRepository incidentLogRepository;
    private final AiAnalysisService aiAnalysisService;
    private final SeverityEngineService severityEngineService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final DashboardService dashboardService;

    public IncidentProcessingServiceImpl(
            IncidentRepository incidentRepository,
            IncidentLogRepository incidentLogRepository,
            AiAnalysisService aiAnalysisService,
            SeverityEngineService severityEngineService,
            NotificationService notificationService,
            AuditService auditService,
            DashboardService dashboardService
    ) {
        this.incidentRepository = incidentRepository;
        this.incidentLogRepository = incidentLogRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.severityEngineService = severityEngineService;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.dashboardService = dashboardService;
    }

    @Override
    @Transactional
    public void processCreatedIncident(IncidentCreatedEvent event) {
        Incident incident = incidentRepository.findById(event.incidentId())
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found for event processing"));

        var logs = incidentLogRepository.findTop20ByIncidentIdOrderByTimestampDesc(incident.getId())
                .stream()
                .map(log -> log.getMessage())
                .toList();

        AiAnalysisRequest aiRequest = new AiAnalysisRequest(
                incident.getIncidentKey(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getService().getName(),
                incident.getAffectedUsers(),
                incident.getResponseTimeMs(),
                incident.getErrorFrequency().doubleValue(),
                logs
        );

        AiAnalysisResult result = aiAnalysisService.analyze(aiRequest);

        incident.setAiSummary(result.executiveSummary());
        incident.setRootCause(result.rootCause());
        incident.setBusinessImpact(result.businessImpact());
        incident.setConfidenceScore(BigDecimal.valueOf(result.confidenceScore()));
        incident.setSuggestedResolution(result.suggestedResolution());
        incident.setSeverity(severityEngineService.classify(incident));
        incident.setStatus(IncidentStatus.INVESTIGATING);

        incidentRepository.save(incident);
        notificationService.notifyIncident(incident);
        auditService.record(AuditAction.INCIDENT_CREATED, "Incident processed " + incident.getIncidentKey(), null);
        dashboardService.evictSummaryCache();
    }
}
