package com.finsight.platform.service.impl;

import com.finsight.platform.dto.response.*;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.service.AnalyticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final IncidentRepository incidentRepository;

    public AnalyticsServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    public AnalyticsOverviewResponse overview() {
        List<TrendPointResponse> trend = incidentRepository.findIncidentTrendLast14Days().stream()
                .map(row -> new TrendPointResponse(((java.sql.Date) row[0]).toLocalDate().toString(), ((Number) row[1]).longValue()))
                .toList();

        List<SeverityDistributionResponse> severityDistribution = incidentRepository.findSeverityDistribution().stream()
                .map(row -> new SeverityDistributionResponse(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

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
