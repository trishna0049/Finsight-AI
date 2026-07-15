# FinSight AI

Enterprise Incident Intelligence Platform for financial services.

## Phase 1 Status

This phase establishes the production-oriented monorepo structure, baseline service scaffolding, and deployment/devops foundation.

## Phase 2-3 Status

Implemented backend-first vertical slices and frontend integration:

- JWT authentication and refresh token flow
- Role-based access control for Platform Admin and Incident Analyst
- Incident simulation workflow with Kafka event processing
- AI analysis integration via dedicated FastAPI service contract
- Severity engine classification and notification/audit side effects
- Incident management APIs (list, details, assign, status, resolve, comments)
- Dashboard summary and service monitoring data APIs
- Log explorer API and frontend page integration
- Analytics overview API (incident trend, severity distribution, top failing services, heatmap)
- Recharts-powered analytics frontend page with operational visualizations
- Initial backend unit tests for severity engine and incident resolution flow
- Incident assignment and timeline comment workflows integrated in frontend
- Analyst assignee directory API for operational triage
- Route-level lazy loading with code splitting to reduce initial bundle size

### Monorepo Apps

- frontend: React + TypeScript + Vite + Tailwind
- backend: Java 21 + Spring Boot 3
- ai-service: Python + FastAPI

### Infrastructure

- PostgreSQL
- Redis
- Kafka + Zookeeper
- Docker Compose orchestration

### Key Principles Applied

- Clean architecture package boundaries
- Separation of concerns across independent deployable services
- Config-first and environment-driven setup
- Enterprise observability baseline with health endpoints
- API documentation readiness with OpenAPI tooling

## Next Planned Phase

- Expand analytics with SLA breach trends and service availability timeline
- Add frontend advanced filters and pagination controls for incidents/logs
- Add controller integration tests and CI test gates

## Local Run (Phase 1)

1. Start infrastructure:
   - docker compose up -d postgres redis zookeeper kafka
2. Start backend:
   - cd backend
   - mvn spring-boot:run
3. Start ai-service:
   - cd ai-service
   - pip install -r requirements.txt
   - uvicorn app.main:app --reload --port 8001
4. Start frontend:
   - cd frontend
   - npm install
   - npm run dev

## Manual Setup Required

You need to configure these values manually before full end-to-end execution:

1. AI provider key:
   - Set OPENAI_API_KEY in root .env (or ai-service/.env)
   - Without this key, AI analysis endpoint will return an error and incident enrichment will be incomplete

2. JWT secret for non-dev environments:
   - Set APP_JWT_SECRET in root .env
   - Use your own Base64-encoded secret with sufficient entropy

3. Local prerequisites:
   - Docker + Docker Compose
   - Java 21 and Maven (for local backend build/test)
   - Python 3.12+ (for ai-service)
   - Node.js 20+ (for frontend)

4. Optional provider change:
   - Set AI_PROVIDER and OPENAI_MODEL in .env if you want a different model/provider configuration

## Architecture Documentation

See docs/architecture.md
