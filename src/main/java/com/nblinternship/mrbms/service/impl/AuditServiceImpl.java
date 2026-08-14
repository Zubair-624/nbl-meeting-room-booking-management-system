package com.nblinternship.mrbms.service.impl;

import com.nblinternship.mrbms.entity.AuditLog;
import com.nblinternship.mrbms.entity.User;
import com.nblinternship.mrbms.repository.AuditLogRepository;
import com.nblinternship.mrbms.repository.UserRepository;
import com.nblinternship.mrbms.service.AuditService;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void log(Integer userId, String action, String module, String ipAddress) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setModule(module);
        auditLog.setIpAddress(ipAddress != null ? ipAddress : "UNKNOWN");
        auditLogRepository.save(auditLog);
    }
}