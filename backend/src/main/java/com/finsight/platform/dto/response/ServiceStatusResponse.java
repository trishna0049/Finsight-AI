package com.finsight.platform.dto.response;

import java.math.BigDecimal;

public record ServiceStatusResponse(
        Long id,
        String name,
        String environment,
        String status,
        BigDecimal latencyMs,
        BigDecimal cpuUsage,
        BigDecimal memoryUsage,
        BigDecimal requestsPerSec,
        BigDecimal availabilityPct
) {
}
