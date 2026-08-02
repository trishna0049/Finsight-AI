package com.finsight.platform.dto.response;

import java.time.OffsetDateTime;

public record TimelineEventResponse(
        OffsetDateTime timestamp,
        String eventType,
        String message,
        String actor
) {
}
