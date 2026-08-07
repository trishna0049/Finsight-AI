package com.finsight.platform.service;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.entity.MonitoredService;
import com.finsight.platform.domain.enums.IncidentStatus;
import com.finsight.platform.dto.request.IncidentResolveRequest;
import com.finsight.platform.repository.CommentRepository;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.repository.UserRepository;
import com.finsight.platform.service.impl.IncidentManagementServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentManagementServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private IncidentManagementServiceImpl service;

    @Test
    void resolveShouldSetResolvedStatusAndPersist() {
        Incident incident = new Incident();
        MonitoredService monitoredService = new MonitoredService();
        monitoredService.setName("Authentication Service");
        incident.setId(101L);
        incident.setIncidentKey("INC-101");
        incident.setService(monitoredService);
        incident.setStatus(IncidentStatus.INVESTIGATING);

        when(incidentRepository.findById(101L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.resolve(101L, new IncidentResolveRequest("Mitigated through failover"), "incident.analyst");

        Assertions.assertEquals("RESOLVED", result.status());
        Assertions.assertEquals("Mitigated through failover", result.resolution());
        verify(incidentRepository).save(any(Incident.class));
        verify(dashboardService).evictSummaryCache();
    }
}
