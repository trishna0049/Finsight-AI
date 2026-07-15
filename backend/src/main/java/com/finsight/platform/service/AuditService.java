package com.finsight.platform.service;

import com.finsight.platform.domain.enums.AuditAction;

public interface AuditService {
    void record(AuditAction action, String details, String username);
}
