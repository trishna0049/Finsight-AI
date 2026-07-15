# FinSight AI Architecture

## Context

FinSight AI is designed as a distributed platform with three independently deployable applications and shared infrastructure services.

## Logical Architecture

1. frontend (React)
   - Operator UI for dashboarding, monitoring, investigation, and simulation.

2. backend (Spring Boot)
   - System of record and orchestration layer.
   - Owns RBAC, incident lifecycle, service health state, audit trail, notifications, and analytics APIs.
   - Uses Kafka for internal event-driven workflows.
   - Uses Redis for dashboard and analytics cache.

3. ai-service (FastAPI)
   - AI inference boundary for root-cause and impact analysis.
   - Provider-configurable LLM orchestration.

## Data and Event Flow (Target)

1. Incident simulator triggers synthetic failure.
2. Backend writes raw logs and incident seed context.
3. Backend emits incident.created event to Kafka.
4. Internal consumer enriches context and requests ai-service analysis.
5. Severity engine computes classification.
6. Incident and timeline updates are persisted.
7. Dashboard cache is invalidated/refreshed in Redis.
8. Notification and audit events are recorded.

## Clean Architecture Mapping (Backend)

- controller: API adapters
- service: use case orchestration
- repository: data access ports
- domain.entity / domain.enums: core business model
- dto + mapper: boundary and transformation contracts
- events: asynchronous integration and decoupled workflows
- security + config + exception: cross-cutting platform concerns

## Non-Functional Priorities

- Security by default: JWT, refresh tokens, RBAC, bcrypt
- Scalability: stateless backend nodes, async event processing, cache-backed read models
- Observability: structured logs, health checks, audit trails
- Operability: containerized services, environment-driven config, CI pipeline
