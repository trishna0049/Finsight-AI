export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error: { code: string; message: string } | null;
  timestamp: string;
}

export interface Tokens {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
}

export interface UserProfile {
  username: string;
  fullName: string;
  email: string;
  roles: string[];
}

export interface DashboardSummary {
  activeIncidents: number;
  criticalIncidents: number;
  openTickets: number;
  resolvedToday: number;
  averageMttrMinutes: number;
  averageResponseTimeMs: number;
}

export interface ServiceStatus {
  id: number;
  name: string;
  environment: string;
  status: string;
  latencyMs: number;
  cpuUsage: number;
  memoryUsage: number;
  requestsPerSec: number;
  availabilityPct: number;
}

export interface LogEntry {
  timestamp: string;
  service: string;
  environment: string;
  logLevel: string;
  correlationId: string;
  responseTimeMs: number;
  errorCode: string | null;
  exception: string | null;
  message: string;
}

export interface IncidentSummary {
  id: number;
  incidentKey: string;
  title: string;
  service: string;
  severity: string;
  status: string;
  assignedTo: string | null;
  affectedUsers: number;
  createdAt: string;
  updatedAt: string;
}

export interface IncidentComment {
  id: number;
  author: string;
  content: string;
  createdAt: string;
}

export interface Assignee {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: string;
}

export interface IncidentDetails extends IncidentSummary {
  description: string;
  responseTimeMs: number;
  errorFrequency: number;
  rootCause: string | null;
  aiSummary: string | null;
  businessImpact: string | null;
  suggestedResolution: string | null;
  confidenceScore: number;
  resolution: string | null;
  resolvedAt: string | null;
  comments: IncidentComment[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface TrendPoint {
  label: string;
  value: number;
}

export interface SeverityDistribution {
  severity: string;
  count: number;
}

export interface ServiceFailure {
  service: string;
  incidents: number;
}

export interface HeatmapPoint {
  day: number;
  hour: number;
  count: number;
}

export interface AnalyticsOverview {
  incidentTrend: TrendPoint[];
  severityDistribution: SeverityDistribution[];
  topFailingServices: ServiceFailure[];
  incidentHeatmap: HeatmapPoint[];
  averageMttrMinutes: number;
  averageResponseTimeMs: number;
}
