package com.finsight.platform.service;

import com.finsight.platform.dto.response.ServiceStatusResponse;

import java.util.List;

public interface MonitoredServiceService {
    List<ServiceStatusResponse> listServices();
}
