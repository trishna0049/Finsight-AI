package com.finsight.platform.dto.response;

import java.util.List;

public record AnalyticsOverviewResponse(
        List<TrendPointResponse> incidentTrend,
        List<TrendPointResponse> slaBreachTrend,
        List<DecimalTrendPointResponse> serviceAvailabilityTrend,
        List<SeverityDistributionResponse> severityDistribution,
        List<ServiceFailureResponse> topFailingServices,
        List<HeatmapPointResponse> incidentHeatmap,
        double averageMttrMinutes,
        double averageResponseTimeMs
) {
}
