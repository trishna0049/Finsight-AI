package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.Comment;
import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.entity.User;
import com.finsight.platform.domain.enums.AuditAction;
import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.domain.enums.IncidentStatus;
import com.finsight.platform.dto.request.IncidentAssignRequest;
import com.finsight.platform.dto.request.IncidentCommentRequest;
import com.finsight.platform.dto.request.IncidentResolveRequest;
import com.finsight.platform.dto.request.IncidentStatusUpdateRequest;
import com.finsight.platform.dto.response.IncidentCommentResponse;
import com.finsight.platform.dto.response.IncidentDetailsResponse;
import com.finsight.platform.dto.response.IncidentSummaryResponse;
import com.finsight.platform.exception.ResourceNotFoundException;
import com.finsight.platform.repository.CommentRepository;
import com.finsight.platform.repository.IncidentRepository;
import com.finsight.platform.repository.UserRepository;
import com.finsight.platform.service.AuditService;
import com.finsight.platform.service.DashboardService;
import com.finsight.platform.service.IncidentManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class IncidentManagementServiceImpl implements IncidentManagementService {

    private final IncidentRepository incidentRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DashboardService dashboardService;
    private final AuditService auditService;

    public IncidentManagementServiceImpl(
            IncidentRepository incidentRepository,
            CommentRepository commentRepository,
            UserRepository userRepository,
            DashboardService dashboardService,
                AuditService auditService
    ) {
        this.incidentRepository = incidentRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
        this.auditService = auditService;
    }

    @Override
    public Page<IncidentSummaryResponse> list(String status, String severity, String service, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, normalizeSort(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Incident> spec = Specification.where(null);

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), IncidentStatus.valueOf(status.toUpperCase())));
        }
        if (severity != null && !severity.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("severity"), IncidentSeverity.valueOf(severity.toUpperCase())));
        }
        if (service != null && !service.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("service").get("name")), "%" + service.toLowerCase() + "%"));
        }

        return incidentRepository.findAll(spec, pageable).map(this::toSummary);
    }

    @Override
    public IncidentDetailsResponse getById(Long incidentId) {
        Incident incident = getIncident(incidentId);
        return toDetails(incident);
    }

    @Override
    @Transactional
    public IncidentDetailsResponse assign(Long incidentId, IncidentAssignRequest request, String actorUsername) {
        Incident incident = getIncident(incidentId);
        User assignee = userRepository.findById(request.assigneeUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        incident.setAssignedTo(assignee);
        incident.setUpdatedAt(OffsetDateTime.now());
        incidentRepository.save(incident);
        auditService.record(AuditAction.INCIDENT_ASSIGNED, "Incident " + incident.getIncidentKey() + " assigned to " + assignee.getUsername(), actorUsername);
        dashboardService.evictSummaryCache();
        return toDetails(incident);
    }

    @Override
    @Transactional
    public IncidentDetailsResponse updateStatus(Long incidentId, IncidentStatusUpdateRequest request) {
        Incident incident = getIncident(incidentId);
        IncidentStatus status = IncidentStatus.valueOf(request.status().toUpperCase());
        incident.setStatus(status);
        incident.setUpdatedAt(OffsetDateTime.now());
        if (status == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(OffsetDateTime.now());
        }
        incidentRepository.save(incident);
        dashboardService.evictSummaryCache();
        return toDetails(incident);
    }

    @Override
    @Transactional
    public IncidentDetailsResponse resolve(Long incidentId, IncidentResolveRequest request, String actorUsername) {
        Incident incident = getIncident(incidentId);
        incident.setResolution(request.resolution());
        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(OffsetDateTime.now());
        incident.setUpdatedAt(OffsetDateTime.now());
        incidentRepository.save(incident);

        auditService.record(AuditAction.INCIDENT_RESOLVED, "Incident " + incident.getIncidentKey() + " resolved", actorUsername);
        dashboardService.evictSummaryCache();
        return toDetails(incident);
    }

    @Override
    @Transactional
    public IncidentCommentResponse addComment(Long incidentId, IncidentCommentRequest request, String actorUsername) {
        Incident incident = getIncident(incidentId);
        User author = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Comment author not found"));

        Comment comment = new Comment();
        comment.setIncident(incident);
        comment.setAuthor(author);
        comment.setContent(request.content());
        comment = commentRepository.save(comment);

        incident.setUpdatedAt(OffsetDateTime.now());
        incidentRepository.save(incident);

        return new IncidentCommentResponse(comment.getId(), author.getUsername(), comment.getContent(), comment.getCreatedAt());
    }

    private Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
    }

    private IncidentSummaryResponse toSummary(Incident incident) {
        return new IncidentSummaryResponse(
                incident.getId(),
                incident.getIncidentKey(),
                incident.getTitle(),
                incident.getService().getName(),
                incident.getSeverity().name(),
                incident.getStatus().name(),
                incident.getAssignedTo() != null ? incident.getAssignedTo().getUsername() : null,
                incident.getAffectedUsers(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }

    private IncidentDetailsResponse toDetails(Incident incident) {
        List<IncidentCommentResponse> comments = commentRepository.findByIncidentIdOrderByCreatedAtAsc(incident.getId())
                .stream()
                .map(comment -> new IncidentCommentResponse(
                        comment.getId(),
                        comment.getAuthor().getUsername(),
                        comment.getContent(),
                        comment.getCreatedAt()
                ))
                .toList();

        return new IncidentDetailsResponse(
                incident.getId(),
                incident.getIncidentKey(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getService().getName(),
                incident.getSeverity().name(),
                incident.getStatus().name(),
                incident.getAssignedTo() != null ? incident.getAssignedTo().getUsername() : null,
                incident.getAffectedUsers(),
                incident.getResponseTimeMs(),
                incident.getErrorFrequency(),
                incident.getRootCause(),
                incident.getAiSummary(),
                incident.getBusinessImpact(),
                incident.getSuggestedResolution(),
                incident.getConfidenceScore(),
                incident.getResolution(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getResolvedAt(),
                comments
        );
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "updatedAt";
        }
        return switch (sortBy) {
            case "createdAt", "updatedAt", "severity", "status" -> sortBy;
            default -> "updatedAt";
        };
    }
}
