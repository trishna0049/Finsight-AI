package com.finsight.platform.service;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.enums.IncidentSeverity;

public interface SeverityEngineService {
    IncidentSeverity classify(Incident incident);
}
