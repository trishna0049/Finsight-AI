package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.service.SeverityEngineService;
import org.springframework.stereotype.Service;

@Service
public class SeverityEngineServiceImpl implements SeverityEngineService {

    @Override
    public IncidentSeverity classify(Incident incident) {
        int score = 0;

        if (incident.getAffectedUsers() >= 2000) {
            score += 40;
        } else if (incident.getAffectedUsers() >= 500) {
            score += 25;
        }

        if (incident.getResponseTimeMs() >= 2500) {
            score += 30;
        } else if (incident.getResponseTimeMs() >= 1000) {
            score += 15;
        }

        if (incident.getErrorFrequency() >= 80) {
            score += 30;
        } else if (incident.getErrorFrequency() >= 30) {
            score += 15;
        }

        String service = incident.getService().getName().toLowerCase();
        if (service.contains("payment") || service.contains("trading") || service.contains("authentication")) {
            score += 15;
        }

        if (score >= 80) {
            return IncidentSeverity.CRITICAL;
        }
        if (score >= 55) {
            return IncidentSeverity.HIGH;
        }
        if (score >= 30) {
            return IncidentSeverity.MEDIUM;
        }
        return IncidentSeverity.LOW;
    }
}
