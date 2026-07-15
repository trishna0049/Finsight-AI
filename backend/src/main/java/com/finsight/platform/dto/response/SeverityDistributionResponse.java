package com.finsight.platform.dto.response;

public record SeverityDistributionResponse(
        String severity,
        long count
) {
}
