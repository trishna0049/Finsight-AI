package com.finsight.platform.service;

import com.finsight.platform.dto.response.IncidentSimulationResponse;

public interface IncidentSimulationService {
    IncidentSimulationResponse simulate(String scenario);
}
