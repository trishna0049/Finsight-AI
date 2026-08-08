# Observability Guide

This repository includes a baseline observability stack centered on Prometheus and Grafana. The implementation is sufficient for local development, internal demos, and basic operational visibility, but it should be treated as a starting point rather than a full production monitoring program.

## Components

- Prometheus scrapes backend metrics from /actuator/prometheus.
- Grafana visualizes the collected metrics through dashboards.
- The backend exposes Spring Boot actuator health and metrics endpoints for operational inspection.

## Local Usage

1. Start the stack:
   - docker compose up -d
2. Open Prometheus:
   - http://localhost:9090
3. Open Grafana:
   - http://localhost:3000
4. Sign in using the configured Grafana credentials:
   - Username: value of GRAFANA_ADMIN_USER
   - Password: value of GRAFANA_ADMIN_PASSWORD

## Deployment Usage

1. Ensure the Prometheus configuration file is present on the target host.
2. Deploy the stack using the repository rollout workflow or the production Compose manifest.
3. Access the services at:
   - Prometheus: http://<host>:9090
   - Grafana: http://<host>:3000

## Operational Endpoints

The backend exposes the following observability-related endpoints:

- /actuator/health
- /actuator/health/readiness
- /actuator/prometheus

## Suggested Initial Dashboard Panels

The following metrics are a sensible first set of dashboard views:

- JVM memory usage (jvm_memory_used_bytes)
- JVM active threads (jvm_threads_live_threads)
- HTTP request count and latency (http_server_requests_seconds)
- HikariCP active connections (hikaricp_connections_active)
- System CPU usage (system_cpu_usage)

## Current Maturity

The observability stack is implemented and usable, but it remains intentionally lightweight. Future improvements could include alerting, log aggregation, richer dashboards, and stronger correlation between incidents and telemetry.
