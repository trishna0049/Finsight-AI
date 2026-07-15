package com.finsight.platform.service;

import com.finsight.platform.dto.request.IncidentAssignRequest;
import com.finsight.platform.dto.request.IncidentCommentRequest;
import com.finsight.platform.dto.request.IncidentResolveRequest;
import com.finsight.platform.dto.request.IncidentStatusUpdateRequest;
import com.finsight.platform.dto.response.IncidentCommentResponse;
import com.finsight.platform.dto.response.IncidentDetailsResponse;
import com.finsight.platform.dto.response.IncidentSummaryResponse;
import org.springframework.data.domain.Page;

public interface IncidentManagementService {
    Page<IncidentSummaryResponse> list(String status, String severity, String service, int page, int size, String sortBy, String direction);

    IncidentDetailsResponse getById(Long incidentId);

    IncidentDetailsResponse assign(Long incidentId, IncidentAssignRequest request, String actorUsername);

    IncidentDetailsResponse updateStatus(Long incidentId, IncidentStatusUpdateRequest request);

    IncidentDetailsResponse resolve(Long incidentId, IncidentResolveRequest request, String actorUsername);

    IncidentCommentResponse addComment(Long incidentId, IncidentCommentRequest request, String actorUsername);
}
