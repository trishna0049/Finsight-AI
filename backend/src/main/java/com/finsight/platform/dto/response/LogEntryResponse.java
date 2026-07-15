package com.finsight.platform.dto.response;

import java.time.OffsetDateTime;

public record LogEntryResponse(
        OffsetDateTime timestamp,
        String service,
        String environment,
        String logLevel,
        String correlationId,
        Integer responseTimeMs,
        String errorCode,
        String exception,
        String message
) {
}
