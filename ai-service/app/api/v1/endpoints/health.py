from datetime import datetime, timezone

from fastapi import APIRouter


router = APIRouter(tags=["System"])


@router.get("/health")
def health() -> dict[str, str]:
    return {
        "status": "UP",
        "service": "finsight-ai-service",
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }
