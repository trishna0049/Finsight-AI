package com.finsight.platform.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record IncidentDetailsResponse(
        Long id,
        String incidentKey,
        String title,
        String description,
        String service,
        String severity,
        String status,
        String assignedTo,
        Integer affectedUsers,
        Integer responseTimeMs,
        Double errorFrequency,
        String rootCause,
        String aiSummary,
        String businessImpact,
        String suggestedResolution,
        Double confidenceScore,
        String resolution,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime resolvedAt,
        List<IncidentCommentResponse> comments
) {
}
