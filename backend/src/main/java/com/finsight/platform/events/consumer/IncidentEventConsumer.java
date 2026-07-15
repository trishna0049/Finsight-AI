package com.finsight.platform.events.consumer;

import com.finsight.platform.events.model.IncidentCreatedEvent;
import com.finsight.platform.service.IncidentProcessingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventConsumer {

    private final IncidentProcessingService incidentProcessingService;

    public IncidentEventConsumer(IncidentProcessingService incidentProcessingService) {
        this.incidentProcessingService = incidentProcessingService;
    }

    @KafkaListener(topics = "${app.kafka.topics.incident-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onIncidentCreated(IncidentCreatedEvent event) {
        incidentProcessingService.processCreatedIncident(event);
    }
}
