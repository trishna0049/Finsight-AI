package com.finsight.platform.service.impl;

import com.finsight.platform.dto.response.LogEntryResponse;
import com.finsight.platform.repository.IncidentLogRepository;
import com.finsight.platform.service.LogExplorerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class LogExplorerServiceImpl implements LogExplorerService {

    private final IncidentLogRepository incidentLogRepository;

    public LogExplorerServiceImpl(IncidentLogRepository incidentLogRepository) {
        this.incidentLogRepository = incidentLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LogEntryResponse> search(String serviceName, String level, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        var allLogs = incidentLogRepository.findAll();

        var filtered = allLogs.stream()
                .filter(log -> serviceName == null || log.getService().getName().equalsIgnoreCase(serviceName))
                .filter(log -> level == null || log.getLogLevel().name().equalsIgnoreCase(level))
                .map(log -> new LogEntryResponse(
                        log.getTimestamp(),
                        log.getService().getName(),
                        log.getEnvironment(),
                        log.getLogLevel().name(),
                        log.getCorrelationId(),
                        log.getResponseTimeMs(),
                        log.getErrorCode(),
                        log.getException(),
                        log.getMessage()
                ))
                .collect(Collectors.toList());

        int from = Math.min((int) pageable.getOffset(), filtered.size());
        int to = Math.min(from + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(from, to), pageable, filtered.size());
    }
}
