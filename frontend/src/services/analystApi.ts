import { httpClient } from "@/services/httpClient";
import type {
  Assignee,
  AnalyticsOverview,
  ApiResponse,
  DashboardSummary,
  IncidentDetails,
  IncidentSummary,
  LogEntry,
  PageResponse,
  ServiceStatus
} from "@/types/api";

export async function fetchDashboardSummary(): Promise<DashboardSummary> {
  const response = await httpClient.get<ApiResponse<DashboardSummary>>("/analyst/dashboard/summary");
  return response.data.data;
}

export async function fetchServices(): Promise<ServiceStatus[]> {
  const response = await httpClient.get<ApiResponse<ServiceStatus[]>>("/analyst/services");
  return response.data.data;
}

export async function simulateIncident(scenario: string): Promise<void> {
  await httpClient.post(`/analyst/simulator/${scenario}`);
}

export async function fetchIncidents(params: {
  page?: number;
  size?: number;
  status?: string;
  severity?: string;
  service?: string;
  sortBy?: string;
  direction?: "asc" | "desc";
}): Promise<PageResponse<IncidentSummary>> {
  const response = await httpClient.get<ApiResponse<PageResponse<IncidentSummary>>>("/analyst/incidents", { params });
  return response.data.data;
}

export async function fetchIncidentDetails(incidentId: number): Promise<IncidentDetails> {
  const response = await httpClient.get<ApiResponse<IncidentDetails>>(`/analyst/incidents/${incidentId}`);
  return response.data.data;
}

export async function fetchAssignees(): Promise<Assignee[]> {
  const response = await httpClient.get<ApiResponse<Assignee[]>>("/analyst/users/assignees");
  return response.data.data;
}

export async function assignIncident(incidentId: number, assigneeUserId: number): Promise<void> {
  await httpClient.patch(`/analyst/incidents/${incidentId}/assign`, { assigneeUserId });
}

export async function addIncidentComment(incidentId: number, content: string): Promise<void> {
  await httpClient.post(`/analyst/incidents/${incidentId}/comments`, { content });
}

export async function resolveIncident(incidentId: number, resolution: string): Promise<void> {
  await httpClient.patch(`/analyst/incidents/${incidentId}/resolve`, { resolution });
}

export async function fetchLogs(params: { page?: number; size?: number; service?: string; level?: string }): Promise<PageResponse<LogEntry>> {
  const response = await httpClient.get<ApiResponse<PageResponse<LogEntry>>>("/analyst/logs", { params });
  return response.data.data;
}

export async function fetchAnalyticsOverview(): Promise<AnalyticsOverview> {
  const response = await httpClient.get<ApiResponse<AnalyticsOverview>>("/analyst/analytics/overview");
  return response.data.data;
}
