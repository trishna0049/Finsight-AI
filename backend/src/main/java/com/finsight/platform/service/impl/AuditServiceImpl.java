package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.AuditLog;
import com.finsight.platform.domain.entity.User;
import com.finsight.platform.domain.enums.AuditAction;
import com.finsight.platform.repository.AuditLogRepository;
import com.finsight.platform.repository.UserRepository;
import com.finsight.platform.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void record(AuditAction action, String details, String username) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setDetails(details);
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            auditLog.setUser(user);
        }
        auditLogRepository.save(auditLog);
    }
}
