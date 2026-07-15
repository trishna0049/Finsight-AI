package com.finsight.platform.service;

import com.finsight.platform.events.model.IncidentCreatedEvent;

public interface IncidentProcessingService {
    void processCreatedIncident(IncidentCreatedEvent event);
}
