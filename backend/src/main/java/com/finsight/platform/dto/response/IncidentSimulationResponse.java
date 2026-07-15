package com.finsight.platform.dto.response;

public record IncidentSimulationResponse(
        Long incidentId,
        String incidentKey,
        String scenario,
        String message
) {
}
