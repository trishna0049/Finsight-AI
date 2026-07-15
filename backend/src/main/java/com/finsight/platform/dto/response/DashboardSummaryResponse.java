package com.finsight.platform.dto.response;

public record DashboardSummaryResponse(
        long activeIncidents,
        long criticalIncidents,
        long openTickets,
        long resolvedToday,
        double averageMttrMinutes,
        double averageResponseTimeMs
) {
}
