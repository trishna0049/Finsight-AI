package com.finsight.platform.controller;

import com.finsight.platform.dto.response.*;
import com.finsight.platform.security.JwtService;
import com.finsight.platform.security.PlatformUserDetailsService;
import com.finsight.platform.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalystController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalystControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private MonitoredServiceService monitoredServiceService;

    @MockBean
    private IncidentSimulationService incidentSimulationService;

    @MockBean
    private LogExplorerService logExplorerService;

    @MockBean
    private IncidentManagementService incidentManagementService;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private UserDirectoryService userDirectoryService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private PlatformUserDetailsService platformUserDetailsService;

    @Test
    void analyticsOverviewShouldReturnPayload() throws Exception {
        AnalyticsOverviewResponse payload = new AnalyticsOverviewResponse(
                List.of(new TrendPointResponse("2026-07-10", 12)),
                List.of(new TrendPointResponse("2026-07-10", 3)),
                List.of(new DecimalTrendPointResponse("2026-07-10", 99.4)),
                List.of(new SeverityDistributionResponse("HIGH", 8)),
                List.of(new ServiceFailureResponse("Payment Service", 5)),
                List.of(new HeatmapPointResponse(1, 9, 3)),
                45.2,
                985.0
        );

        when(analyticsService.overview()).thenReturn(payload);

        mockMvc.perform(get("/api/v1/analyst/analytics/overview").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageMttrMinutes").value(45.2))
                .andExpect(jsonPath("$.data.slaBreachTrend[0].value").value(3));
    }

    @Test
    void assigneesShouldReturnActiveUsers() throws Exception {
        when(userDirectoryService.listAssignableUsers()).thenReturn(List.of(
                new AssigneeResponse(1L, "incident.analyst", "Incident Analyst", "incident.analyst@finsight.local", "INCIDENT_ANALYST")
        ));

        mockMvc.perform(get("/api/v1/analyst/users/assignees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].username").value("incident.analyst"));
    }

    @Test
    void incidentsShouldReturnPagedResult() throws Exception {
        IncidentSummaryResponse incident = new IncidentSummaryResponse(
                10L,
                "INC-100010",
                "Payment timeout spike",
                "Payment Service",
                "HIGH",
                "OPEN",
                "incident.analyst",
                1500,
                java.time.OffsetDateTime.now(),
                java.time.OffsetDateTime.now()
        );

        when(incidentManagementService.list(null, null, null, 0, 25, "updatedAt", "desc"))
                .thenReturn(new PageImpl<>(List.of(incident)));

        mockMvc.perform(get("/api/v1/analyst/incidents").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].incidentKey").value("INC-100010"));
    }

    @Test
    void simulateShouldReturnIncidentSeedResponse() throws Exception {
        when(incidentSimulationService.simulate("payment-failure"))
                .thenReturn(new IncidentSimulationResponse(
                        5001L,
                        "INC-500001",
                        "payment-failure",
                        "Simulation accepted"
                ));

        mockMvc.perform(post("/api/v1/analyst/simulator/payment-failure").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.incidentKey").value("INC-500001"));
    }
}
