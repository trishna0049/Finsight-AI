package com.finsight.platform.service;

import com.finsight.platform.dto.response.DashboardSummaryResponse;

public interface DashboardService {
    DashboardSummaryResponse getSummary();

    void evictSummaryCache();
}
