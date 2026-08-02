package com.finsight.platform.service.impl;

import com.finsight.platform.dto.response.*;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.repository.MetricRepository;
import com.finsight.platform.service.AnalyticsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final IncidentRepository incidentRepository;
        private final MetricRepository metricRepository;

        public AnalyticsServiceImpl(IncidentRepository incidentRepository, MetricRepository metricRepository) {
        this.incidentRepository = incidentRepository;
                this.metricRepository = metricRepository;
    }

    @Override
    public AnalyticsOverviewResponse overview() {
        List<TrendPointResponse> trend = incidentRepository.findIncidentTrendLast14Days().stream()
                .map(row -> new TrendPointResponse(((java.sql.Date) row[0]).toLocalDate().toString(), ((Number) row[1]).longValue()))
                .toList();

        List<SeverityDistributionResponse> severityDistribution = incidentRepository.findSeverityDistribution().stream()
                .map(row -> new SeverityDistributionResponse(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

        List<TrendPointResponse> slaBreachTrend = incidentRepository.findSlaBreachTrendLast14Days().stream()
                .map(row -> new TrendPointResponse(((java.sql.Date) row[0]).toLocalDate().toString(), ((Number) row[1]).longValue()))
                .toList();

        List<DecimalTrendPointResponse> availabilityRaw = metricRepository.findServiceAvailabilityTrendLast14Days().stream()
                .map(row -> new DecimalTrendPointResponse(
                        String.valueOf(row[0]),
                        round2(((Number) row[1]).doubleValue())
                ))
                .toList();

        // Reverse newest-first DB results for chronological chart rendering.
        List<DecimalTrendPointResponse> serviceAvailabilityTrend = new ArrayList<>(availabilityRaw);
        java.util.Collections.reverse(serviceAvailabilityTrend);

        List<ServiceFailureResponse> topFailingServices = incidentRepository.findTopFailingServices().stream()
                .map(row -> new ServiceFailureResponse(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

        List<HeatmapPointResponse> heatmap = incidentRepository.findIncidentHeatmapLast14Days().stream()
                .map(row -> new HeatmapPointResponse(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();

        double avgMttr = incidentRepository.findAverageMttrMinutes() == null ? 0d : incidentRepository.findAverageMttrMinutes();
        double avgResponse = incidentRepository.findAverageResponseTimeMs() == null ? 0d : incidentRepository.findAverageResponseTimeMs();

        return new AnalyticsOverviewResponse(
                trend,
                slaBreachTrend,
                serviceAvailabilityTrend,
                severityDistribution,
                topFailingServices,
                heatmap,
                round2(avgMttr),
                round2(avgResponse)
        );
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
