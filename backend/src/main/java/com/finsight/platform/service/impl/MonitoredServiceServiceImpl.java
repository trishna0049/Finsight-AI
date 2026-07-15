package com.finsight.platform.service.impl;

import com.finsight.platform.dto.response.ServiceStatusResponse;
import com.finsight.platform.repository.MonitoredServiceRepository;
import com.finsight.platform.service.MonitoredServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitoredServiceServiceImpl implements MonitoredServiceService {

    private final MonitoredServiceRepository monitoredServiceRepository;

    public MonitoredServiceServiceImpl(MonitoredServiceRepository monitoredServiceRepository) {
        this.monitoredServiceRepository = monitoredServiceRepository;
    }

    @Override
    public List<ServiceStatusResponse> listServices() {
        return monitoredServiceRepository.findAll().stream()
                .map(service -> new ServiceStatusResponse(
                        service.getId(),
                        service.getName(),
                        service.getEnvironment(),
                        service.getStatus().name(),
                        service.getLatencyMs(),
                        service.getCpuUsage(),
                        service.getMemoryUsage(),
                        service.getRequestsPerSec(),
                        service.getAvailabilityPct()
                ))
                .toList();
    }
}
