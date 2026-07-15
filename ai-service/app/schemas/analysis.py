from pydantic import BaseModel, Field


class IncidentAnalysisRequest(BaseModel):
    incident_id: str
    title: str
    description: str
    service_name: str
    affected_users: int = Field(default=0, ge=0)
    response_time_ms: int = Field(default=0, ge=0)
    error_frequency: float = Field(default=0, ge=0)
    recent_logs: list[str] = Field(default_factory=list)


class IncidentAnalysisResponse(BaseModel):
    executive_summary: str
    root_cause: str
    business_impact: str
    confidence_score: float
    suggested_resolution: str
