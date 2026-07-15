package com.finsight.platform.service.impl;

import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.domain.enums.IncidentStatus;
import com.finsight.platform.dto.response.DashboardSummaryResponse;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.service.DashboardService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final IncidentRepository incidentRepository;

    public DashboardServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    @Cacheable(cacheNames = "dashboardSummary", key = "'global'")
    public DashboardSummaryResponse getSummary() {
        long active = incidentRepository.countByStatus(IncidentStatus.OPEN)
                + incidentRepository.countByStatus(IncidentStatus.INVESTIGATING);
        long critical = incidentRepository.countBySeverity(IncidentSeverity.CRITICAL);
        long open = incidentRepository.countByStatus(IncidentStatus.OPEN);

        OffsetDateTime startOfDay = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long resolvedToday = incidentRepository.countByStatusAndUpdatedAtBetween(
                IncidentStatus.RESOLVED,
                startOfDay,
                OffsetDateTime.now()
        );

        double mttr = incidentRepository.findAverageMttrMinutes() == null ? 0d : incidentRepository.findAverageMttrMinutes();
        double avgResponse = incidentRepository.findAverageResponseTimeMs() == null ? 0d : incidentRepository.findAverageResponseTimeMs();

        return new DashboardSummaryResponse(active, critical, open, resolvedToday, mttr, avgResponse);
    }

    @Override
    @CacheEvict(cacheNames = "dashboardSummary", key = "'global'")
    public void evictSummaryCache() {
    }
}
