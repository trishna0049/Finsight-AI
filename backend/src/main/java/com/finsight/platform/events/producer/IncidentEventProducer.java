package com.finsight.platform.events.producer;

import com.finsight.platform.events.model.IncidentCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String incidentCreatedTopic;

    public IncidentEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${app.kafka.topics.incident-created}") String incidentCreatedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.incidentCreatedTopic = incidentCreatedTopic;
    }

    public void publishIncidentCreated(IncidentCreatedEvent event) {
        kafkaTemplate.send(incidentCreatedTopic, String.valueOf(event.incidentId()), event);
    }
}
