package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.entity.Notification;
import com.finsight.platform.domain.enums.NotificationChannel;
import com.finsight.platform.domain.enums.NotificationStatus;
import com.finsight.platform.repository.NotificationRepository;
import com.finsight.platform.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void notifyIncident(Incident incident) {
        Notification email = new Notification();
        email.setIncident(incident);
        email.setChannel(NotificationChannel.EMAIL);
        email.setRecipient("oncall@finsight.local");
        email.setMessage("Incident " + incident.getIncidentKey() + " requires attention. Severity: " + incident.getSeverity());
        email.setStatus(NotificationStatus.SENT);
        notificationRepository.save(email);

        Notification slack = new Notification();
        slack.setIncident(incident);
        slack.setChannel(NotificationChannel.SLACK);
        slack.setRecipient("#incident-war-room");
        slack.setMessage("[ALERT] " + incident.getTitle() + " | " + incident.getSeverity());
        slack.setStatus(NotificationStatus.SENT);
        notificationRepository.save(slack);
    }
}
