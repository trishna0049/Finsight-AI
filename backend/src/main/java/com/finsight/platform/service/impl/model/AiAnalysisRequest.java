package com.finsight.platform.service.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiAnalysisRequest(
        @JsonProperty("incident_id") String incidentId,
        String title,
        String description,
        @JsonProperty("service_name") String serviceName,
        @JsonProperty("affected_users") int affectedUsers,
        @JsonProperty("response_time_ms") int responseTimeMs,
        @JsonProperty("error_frequency") double errorFrequency,
        @JsonProperty("recent_logs") List<String> recentLogs
) {
}
