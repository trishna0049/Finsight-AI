from fastapi import APIRouter, HTTPException

from app.schemas.analysis import IncidentAnalysisRequest, IncidentAnalysisResponse
from app.services.llm_service import LlmService


router = APIRouter(prefix="/analysis", tags=["Analysis"])
llm_service = LlmService()


@router.post("/incident", response_model=IncidentAnalysisResponse)
def analyze_incident(payload: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    try:
        result = llm_service.analyze_incident(payload)
        return IncidentAnalysisResponse(**result)
    except ValueError as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc
    except Exception as exc:  # noqa: BLE001
        raise HTTPException(status_code=500, detail=f"AI analysis failed: {exc}") from exc
