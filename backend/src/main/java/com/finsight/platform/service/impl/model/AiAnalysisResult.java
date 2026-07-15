package com.finsight.platform.service.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiAnalysisResult(
        @JsonProperty("executive_summary") String executiveSummary,
        @JsonProperty("root_cause") String rootCause,
        @JsonProperty("business_impact") String businessImpact,
        @JsonProperty("confidence_score") double confidenceScore,
        @JsonProperty("suggested_resolution") String suggestedResolution
) {
}
