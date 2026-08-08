# FinSight AI Architecture

## Context

FinSight AI is a containerized incident intelligence platform composed of a React frontend, a Spring Boot backend, a FastAPI AI service, and shared infrastructure services for PostgreSQL, Redis, Kafka, Prometheus, and Grafana. The current implementation is best understood as an engineering prototype and internal operations demo rather than a fully hardened production platform.

## Logical Architecture

1. frontend (React + TypeScript)
   - Provides operator-facing screens for dashboarding, service monitoring, incident management, simulation, logs, and analytics.

2. backend (Spring Boot 3)
   - Acts as the system of record and orchestration layer.
   - Owns authentication, authorization, incident lifecycle workflows, service health state, audit trail, notifications, and analytics APIs.
   - Uses Kafka for event-driven incident processing and Redis for dashboard summary caching.

3. ai-service (FastAPI)
   - Exposes an AI analysis boundary for incident enrichment.
   - Receives incident context and recent logs, invokes the configured OpenAI model, and returns structured analysis fields.

## Runtime View

1. An operator authenticates through the frontend and receives a JWT from the backend.
2. The frontend issues authenticated requests to backend REST endpoints for dashboard data, incidents, logs, analytics, and simulations.
3. The backend reads and writes relational data in PostgreSQL and uses Redis for cached dashboard summaries.
4. When an incident simulation is triggered, the backend emits an incident-created event to Kafka.
5. The consumer processes the event, gathers recent logs, calls the AI service, updates the incident record, and triggers notification/audit side effects.

## Backend Structure

The backend follows a layered structure aligned to its domain services:

- controller: REST API adapters
- service: orchestration and business workflows
- repository: persistence access and query behavior
- domain.entity / domain.enums: core business model
- dto + mapper: API boundary and transformation contracts
- events: Kafka producers and consumers
- security + config + exception: cross-cutting platform concerns

## Non-Functional Priorities

- Security: JWT-based authentication, refresh tokens, RBAC, and BCrypt password hashing
- Scalability: stateless backend services, async event processing, and cache-backed read models
- Observability: health endpoints, Prometheus metrics, structured service logging, and audit activity
- Operability: containerized services, environment-driven configuration, and CI/CD automation
