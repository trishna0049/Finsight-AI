# Observability Guide

This project provides a baseline observability stack using Prometheus and Grafana.

## Components

- Prometheus scrapes backend metrics from /actuator/prometheus
- Grafana visualizes Prometheus metrics through dashboards

## Local Usage

1. Start stack:
   - docker compose up -d
2. Open Prometheus:
   - http://localhost:9090
3. Open Grafana:
   - http://localhost:3000
4. Login with:
   - Username: value of GRAFANA_ADMIN_USER
   - Password: value of GRAFANA_ADMIN_PASSWORD

## Production Usage

1. Ensure observability/prometheus.yml exists on deployment host.
2. Deploy via .github/workflows/rollout.yml.
3. Access endpoints:
   - Prometheus: http://<host>:9090
   - Grafana: http://<host>:3000

## Suggested First Dashboard Panels

- JVM memory used (jvm_memory_used_bytes)
- JVM threads live (jvm_threads_live_threads)
- HTTP server request count and latency (http_server_requests_seconds)
- HikariCP active connections (hikaricp_connections_active)
- System CPU usage (system_cpu_usage)
