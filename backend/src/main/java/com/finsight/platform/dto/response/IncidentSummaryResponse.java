package com.finsight.platform.dto.response;

import java.time.OffsetDateTime;

public record IncidentSummaryResponse(
        Long id,
        String incidentKey,
        String title,
        String service,
        String severity,
        String status,
        String assignedTo,
        Integer affectedUsers,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
