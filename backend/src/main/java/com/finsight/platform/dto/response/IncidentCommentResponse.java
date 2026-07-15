package com.finsight.platform.dto.response;

import java.time.OffsetDateTime;

public record IncidentCommentResponse(
        Long id,
        String author,
        String content,
        OffsetDateTime createdAt
) {
}
