CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(120) NOT NULL UNIQUE,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(160) NOT NULL UNIQUE,
    environment VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    latency_ms NUMERIC(10,2) NOT NULL DEFAULT 0,
    cpu_usage NUMERIC(10,2) NOT NULL DEFAULT 0,
    memory_usage NUMERIC(10,2) NOT NULL DEFAULT 0,
    requests_per_sec NUMERIC(10,2) NOT NULL DEFAULT 0,
    availability_pct NUMERIC(5,2) NOT NULL DEFAULT 99.99,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    incident_key VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(240) NOT NULL,
    description TEXT NOT NULL,
    service_id BIGINT NOT NULL REFERENCES services(id),
    severity VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    assigned_to BIGINT REFERENCES users(id),
    affected_users INT NOT NULL DEFAULT 0,
    response_time_ms INT NOT NULL DEFAULT 0,
    error_frequency NUMERIC(8,2) NOT NULL DEFAULT 0,
    root_cause TEXT,
    ai_summary TEXT,
    business_impact TEXT,
    suggested_resolution TEXT,
    confidence_score NUMERIC(6,4) NOT NULL DEFAULT 0,
    resolution TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ
);

CREATE TABLE incident_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    service_id BIGINT NOT NULL REFERENCES services(id),
    environment VARCHAR(32) NOT NULL,
    log_level VARCHAR(16) NOT NULL,
    correlation_id VARCHAR(120) NOT NULL,
    response_time_ms INT NOT NULL,
    error_code VARCHAR(64),
    exception VARCHAR(255),
    message TEXT NOT NULL,
    incident_id BIGINT REFERENCES incidents(id)
);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id),
    author_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id),
    channel VARCHAR(32) NOT NULL,
    recipient VARCHAR(160) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(64) NOT NULL,
    details TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE metrics (
    id BIGSERIAL PRIMARY KEY,
    metric_name VARCHAR(80) NOT NULL,
    metric_value NUMERIC(14,2) NOT NULL,
    metric_date DATE NOT NULL
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_severity ON incidents(severity);
CREATE INDEX idx_incidents_service ON incidents(service_id);
CREATE INDEX idx_incident_logs_service_ts ON incident_logs(service_id, timestamp DESC);
CREATE INDEX idx_incident_logs_level ON incident_logs(log_level);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at DESC);

INSERT INTO roles(name) VALUES
('ROLE_PLATFORM_ADMIN'),
('ROLE_INCIDENT_ANALYST');

INSERT INTO users(username, email, password_hash, full_name, active)
VALUES
('platform.admin', 'platform.admin@finsight.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOeR7N6vVsQt1zAr72Xd1LSeX776BF3Se', 'Platform Admin', TRUE),
('incident.analyst', 'incident.analyst@finsight.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOeR7N6vVsQt1zAr72Xd1LSeX776BF3Se', 'Incident Analyst', TRUE);

INSERT INTO user_roles(user_id, role_id)
VALUES
(1, 1),
(2, 2);

INSERT INTO users(username, email, password_hash, full_name, active)
SELECT
    'user' || gs::TEXT,
    'user' || gs::TEXT || '@finsight.local',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOeR7N6vVsQt1zAr72Xd1LSeX776BF3Se',
    'FinSight User ' || gs::TEXT,
    TRUE
FROM generate_series(3, 200) gs;

INSERT INTO user_roles(user_id, role_id)
SELECT id,
       CASE WHEN random() < 0.2 THEN 1 ELSE 2 END
FROM users
WHERE id >= 3;

INSERT INTO services(name, environment, status, latency_ms, cpu_usage, memory_usage, requests_per_sec, availability_pct)
SELECT s.name,
       'PROD',
       CASE
           WHEN random() < 0.75 THEN 'HEALTHY'
           WHEN random() < 0.9 THEN 'DEGRADED'
           ELSE 'DOWN'
       END,
       round((random() * 500 + 30)::numeric, 2),
       round((random() * 90 + 5)::numeric, 2),
       round((random() * 90 + 5)::numeric, 2),
       round((random() * 1200 + 20)::numeric, 2),
       round((98 + random() * 2)::numeric, 2)
FROM (
    VALUES
        ('Payment Service'),
        ('Authentication Service'),
        ('Customer Service'),
        ('Notification Service'),
        ('Trading Engine'),
        ('Database Cluster'),
        ('Risk Scoring Service'),
        ('Fraud Detection Service'),
        ('Ledger Service'),
        ('Card Processing Service'),
        ('KYC Service'),
        ('AML Service'),
        ('Portfolio Service'),
        ('Settlement Service'),
        ('Market Data Gateway'),
        ('Reporting Service'),
        ('Document Service'),
        ('API Gateway'),
        ('Session Service'),
        ('Batch Reconciliation Service')
) AS s(name);

INSERT INTO incidents(
    incident_key,
    title,
    description,
    service_id,
    severity,
    status,
    assigned_to,
    affected_users,
    response_time_ms,
    error_frequency,
    root_cause,
    ai_summary,
    business_impact,
    suggested_resolution,
    confidence_score,
    resolution,
    created_at,
    updated_at,
    resolved_at
)
SELECT
    'INC-' || to_char(100000 + gs, 'FM999999'),
    CASE WHEN random() < 0.5 THEN 'Latency spike detected' ELSE 'Service failure detected' END,
    'Auto-seeded enterprise incident for platform realism',
    (SELECT id FROM services ORDER BY random() LIMIT 1),
    (ARRAY['LOW','MEDIUM','HIGH','CRITICAL'])[floor(random() * 4 + 1)],
    (ARRAY['OPEN','INVESTIGATING','RESOLVED','CLOSED'])[floor(random() * 4 + 1)],
    (SELECT id FROM users WHERE id > 2 ORDER BY random() LIMIT 1),
    floor(random() * 6000 + 10)::int,
    floor(random() * 3200 + 50)::int,
    round((random() * 120)::numeric, 2),
    'Upstream dependency instability',
    'AI summary generated during seed initialization',
    'Impacts customer transaction flow and back-office operations',
    'Restart unhealthy nodes, drain traffic, and validate dependencies',
    round((0.50 + random() * 0.49)::numeric, 4),
    CASE WHEN random() < 0.45 THEN 'Resolved by failover procedure' ELSE NULL END,
    NOW() - ((random() * 30)::int || ' days')::interval,
    NOW() - ((random() * 12)::int || ' hours')::interval,
    CASE
        WHEN random() < 0.45 THEN NOW() - ((random() * 2)::int || ' hours')::interval
        ELSE NULL
    END
FROM generate_series(1, 500) gs;

INSERT INTO incident_logs(
    timestamp,
    service_id,
    environment,
    log_level,
    correlation_id,
    response_time_ms,
    error_code,
    exception,
    message,
    incident_id
)
SELECT
    NOW() - ((random() * 14)::int || ' days')::interval,
    (SELECT id FROM services ORDER BY random() LIMIT 1),
    'PROD',
    (ARRAY['INFO', 'WARN', 'ERROR'])[floor(random() * 3 + 1)],
    md5(gs::TEXT || random()::TEXT),
    floor(random() * 3500 + 20)::int,
    CASE WHEN random() < 0.35 THEN 'ERR-' || floor(random() * 9999 + 1)::int::TEXT ELSE NULL END,
    CASE
        WHEN random() < 0.15 THEN 'java.net.SocketTimeoutException'
        WHEN random() < 0.30 THEN 'org.postgresql.util.PSQLException'
        ELSE NULL
    END,
    CASE
        WHEN random() < 0.3 THEN 'Timeout while calling upstream dependency'
        WHEN random() < 0.6 THEN 'Request processed successfully'
        ELSE 'Retry executed due to transient network error'
    END,
    CASE WHEN random() < 0.55 THEN (SELECT id FROM incidents ORDER BY random() LIMIT 1) ELSE NULL END
FROM generate_series(1, 20000) gs;

INSERT INTO metrics(metric_name, metric_value, metric_date)
SELECT metric_name, round((random() * 1000 + 10)::numeric, 2), current_date - ((random() * 30)::int)
FROM (
    VALUES
        ('avg_mttr_minutes'),
        ('avg_response_ms'),
        ('incident_volume'),
        ('service_availability_pct')
) m(metric_name),
generate_series(1, 40);

INSERT INTO audit_logs(user_id, action, details, created_at)
SELECT
    (SELECT id FROM users ORDER BY random() LIMIT 1),
    (ARRAY['LOGIN','LOGOUT','INCIDENT_CREATED','INCIDENT_ASSIGNED','INCIDENT_RESOLVED','USER_CREATED'])[floor(random() * 6 + 1)],
    'Seeded audit event',
    NOW() - ((random() * 20)::int || ' days')::interval
FROM generate_series(1, 2000);
