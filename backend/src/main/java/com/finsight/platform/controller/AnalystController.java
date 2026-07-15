package com.finsight.platform.controller;

import com.finsight.platform.dto.response.*;
import com.finsight.platform.service.DashboardService;
import com.finsight.platform.service.IncidentManagementService;
import com.finsight.platform.service.IncidentSimulationService;
import com.finsight.platform.service.LogExplorerService;
import com.finsight.platform.service.MonitoredServiceService;
import com.finsight.platform.service.AnalyticsService;
import com.finsight.platform.service.UserDirectoryService;
import com.finsight.platform.dto.request.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analyst")
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN','INCIDENT_ANALYST')")
public class AnalystController {

    private final DashboardService dashboardService;
    private final MonitoredServiceService monitoredServiceService;
    private final IncidentSimulationService incidentSimulationService;
    private final LogExplorerService logExplorerService;
    private final IncidentManagementService incidentManagementService;
    private final AnalyticsService analyticsService;
    private final UserDirectoryService userDirectoryService;

    public AnalystController(
            DashboardService dashboardService,
            MonitoredServiceService monitoredServiceService,
            IncidentSimulationService incidentSimulationService,
            LogExplorerService logExplorerService,
            IncidentManagementService incidentManagementService,
            AnalyticsService analyticsService,
            UserDirectoryService userDirectoryService
    ) {
        this.dashboardService = dashboardService;
        this.monitoredServiceService = monitoredServiceService;
        this.incidentSimulationService = incidentSimulationService;
        this.logExplorerService = logExplorerService;
        this.incidentManagementService = incidentManagementService;
        this.analyticsService = analyticsService;
        this.userDirectoryService = userDirectoryService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> dashboardSummary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary()));
    }

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<ServiceStatusResponse>>> services() {
        return ResponseEntity.ok(ApiResponse.success(monitoredServiceService.listServices()));
    }

    @PostMapping("/simulator/{scenario}")
    public ResponseEntity<ApiResponse<IncidentSimulationResponse>> simulate(@PathVariable String scenario) {
        return ResponseEntity.ok(ApiResponse.success(incidentSimulationService.simulate(scenario)));
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<LogEntryResponse>>> logs(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(logExplorerService.search(service, level, page, size)));
    }

    @GetMapping("/analytics/overview")
    public ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> analyticsOverview() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.overview()));
    }

    @GetMapping("/users/assignees")
    public ResponseEntity<ApiResponse<List<AssigneeResponse>>> assignees() {
        return ResponseEntity.ok(ApiResponse.success(userDirectoryService.listAssignableUsers()));
    }

    @GetMapping("/incidents")
    public ResponseEntity<ApiResponse<Page<IncidentSummaryResponse>>> incidents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String service,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                incidentManagementService.list(status, severity, service, page, size, sortBy, direction)
        ));
    }

    @GetMapping("/incidents/{incidentId}")
    public ResponseEntity<ApiResponse<IncidentDetailsResponse>> incidentById(@PathVariable Long incidentId) {
        return ResponseEntity.ok(ApiResponse.success(incidentManagementService.getById(incidentId)));
    }

    @PatchMapping("/incidents/{incidentId}/assign")
    public ResponseEntity<ApiResponse<IncidentDetailsResponse>> assign(
            @PathVariable Long incidentId,
            @Valid @RequestBody IncidentAssignRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                incidentManagementService.assign(incidentId, request, authentication.getName())
        ));
    }

    @PatchMapping("/incidents/{incidentId}/status")
    public ResponseEntity<ApiResponse<IncidentDetailsResponse>> updateStatus(
            @PathVariable Long incidentId,
            @Valid @RequestBody IncidentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                incidentManagementService.updateStatus(incidentId, request)
        ));
    }

    @PatchMapping("/incidents/{incidentId}/resolve")
    public ResponseEntity<ApiResponse<IncidentDetailsResponse>> resolve(
            @PathVariable Long incidentId,
            @Valid @RequestBody IncidentResolveRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                incidentManagementService.resolve(incidentId, request, authentication.getName())
        ));
    }

    @PostMapping("/incidents/{incidentId}/comments")
    public ResponseEntity<ApiResponse<IncidentCommentResponse>> addComment(
            @PathVariable Long incidentId,
            @Valid @RequestBody IncidentCommentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                incidentManagementService.addComment(incidentId, request, authentication.getName())
        ));
    }
}
