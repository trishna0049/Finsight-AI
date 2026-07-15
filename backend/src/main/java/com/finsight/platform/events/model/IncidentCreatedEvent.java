package com.finsight.platform.events.model;

import java.time.OffsetDateTime;

public record IncidentCreatedEvent(
        Long incidentId,
        String scenario,
        OffsetDateTime occurredAt
) {
}
