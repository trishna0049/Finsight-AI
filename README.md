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
- Advanced incident/log filtering, sorting, and pagination controls in frontend
- Analytics expansion with SLA breach and service availability trends
- Backend controller integration test coverage for analyst endpoints
- Incident status transition controls and unified timeline view in frontend
- Production deployment workflow for GHCR image publishing
- Production compose manifest for image-based deployments
- Controller integration tests expanded for auth and simulation endpoints
- Environment-based rollout workflow for staging/production over SSH
- Prometheus and Grafana observability stack for local and production compose
- Rollout health verification gate using backend readiness endpoint

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

- Add E2E smoke tests for critical analyst journeys
- Add centralized log aggregation (Loki/Promtail) with incident correlation labels
- Add canary rollout and rollback automation in deployment workflow

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

5. Grafana local/admin credentials:
   - Set GRAFANA_ADMIN_USER and GRAFANA_ADMIN_PASSWORD in .env/.env.prod
   - Replace defaults before internet-exposed deployments

6. GitHub repository secrets (for CI/CD and deployments):
   - APP_JWT_SECRET
   - OPENAI_API_KEY (required only when running live AI analysis in deployed environments)

7. Production deployment variables:
   - Create a .env file from .env.prod.example when using docker-compose.prod.yml
   - Set REPO_OWNER to your GitHub username/org and IMAGE_TAG to a published tag
   - APP_JWT_SECRET must be explicitly set in production (no default fallback)

8. GitHub package permissions:
   - Deploy workflow pushes to GHCR using GITHUB_TOKEN
   - Ensure Actions has permission to write packages in repository settings

9. Rollout workflow secrets (required for .github/workflows/rollout.yml):
   - DEPLOY_HOST
   - DEPLOY_USER
   - DEPLOY_SSH_KEY
   - DEPLOY_PATH
   - GHCR_PAT
   - POSTGRES_PASSWORD
   - APP_JWT_SECRET
   - OPENAI_API_KEY

10. Rollout workflow environment variables (set per GitHub Environment):
   - POSTGRES_DB (optional, default finsight)
   - POSTGRES_USER (optional, default finsight)
   - AI_PROVIDER (optional, default openai)
   - OPENAI_MODEL (optional, default gpt-4o-mini)
   - GRAFANA_ADMIN_USER (optional, default admin)
   - GRAFANA_ADMIN_PASSWORD (optional, default admin)

11. Rollout host prerequisites:
   - curl must be installed on target host for readiness verification step

## Architecture Documentation

See docs/architecture.md

## Observability Documentation

See docs/observability.md

## Deployment (Images)

1. Publish images via GitHub Actions:
   - Run workflow: .github/workflows/deploy.yml
   - Or push tag: git tag v1.0.0 && git push origin v1.0.0

2. Deploy with production compose:
   - cp .env.prod.example .env
   - Edit .env values
   - docker compose -f docker-compose.prod.yml up -d

## Deployment (Environment Rollout)

1. Configure GitHub Environments:
   - Create environments named staging and production
   - Add the required secrets/variables listed above in each environment

2. Run rollout workflow:
   - Trigger .github/workflows/rollout.yml
   - Choose target_environment and image_tag
