package com.finsight.platform.service;

import com.finsight.platform.domain.entity.Incident;

public interface NotificationService {
    void notifyIncident(Incident incident);
}
