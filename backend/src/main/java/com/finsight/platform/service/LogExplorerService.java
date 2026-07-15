package com.finsight.platform.service;

import com.finsight.platform.dto.response.LogEntryResponse;
import org.springframework.data.domain.Page;

public interface LogExplorerService {
    Page<LogEntryResponse> search(String serviceName, String level, int page, int size);
}
