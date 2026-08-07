from openai import OpenAI

from app.core.config import settings
from app.schemas.analysis import IncidentAnalysisRequest


class LlmService:
    def __init__(self) -> None:
        self.client = OpenAI(api_key=settings.openai_api_key) if settings.openai_api_key else None

    def analyze_incident(self, request: IncidentAnalysisRequest) -> dict[str, str | float]:
        if not self.client:
            raise ValueError("OPENAI_API_KEY is not configured")

        prompt = self._build_prompt(request)

        completion = self.client.responses.create(
            model=settings.openai_model,
            input=prompt,
            temperature=0.2,
        )

        content = completion.output_text
        sections = self._parse_response(content)
        return sections

    @staticmethod
    def _build_prompt(request: IncidentAnalysisRequest) -> str:
        logs = "\n".join(request.recent_logs[:20])
        return (
            "You are an enterprise incident analyst for a Tier-1 bank. "
            "Analyze the incident and return concise sections exactly in this format:\n"
            "Executive Summary: ...\n"
            "Root Cause: ...\n"
            "Business Impact: ...\n"
            "Confidence Score: <0 to 1>\n"
            "Suggested Resolution: ...\n\n"
            f"Incident ID: {request.incident_id}\n"
            f"Title: {request.title}\n"
            f"Description: {request.description}\n"
            f"Service: {request.service_name}\n"
            f"Affected Users: {request.affected_users}\n"
            f"Response Time (ms): {request.response_time_ms}\n"
            f"Error Frequency (/min): {request.error_frequency}\n"
            f"Recent Logs:\n{logs}"
        )

    @staticmethod
    def _parse_response(text: str) -> dict[str, str | float]:
        data: dict[str, str | float] = {
            "executive_summary": "",
            "root_cause": "",
            "business_impact": "",
            "confidence_score": 0.0,
            "suggested_resolution": "",
        }

        for line in text.splitlines():
            if line.lower().startswith("executive summary:"):
                data["executive_summary"] = line.split(":", 1)[1].strip()
            elif line.lower().startswith("root cause:"):
                data["root_cause"] = line.split(":", 1)[1].strip()
            elif line.lower().startswith("business impact:"):
                data["business_impact"] = line.split(":", 1)[1].strip()
            elif line.lower().startswith("confidence score:"):
                raw = line.split(":", 1)[1].strip()
                try:
                    data["confidence_score"] = float(raw)
                except ValueError:
                    data["confidence_score"] = 0.5
            elif line.lower().startswith("suggested resolution:"):
                data["suggested_resolution"] = line.split(":", 1)[1].strip()

        return data
