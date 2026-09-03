# TenderOps API

Minimal Java Spring Boot API for TenderOps Lab. PostgreSQL is required at runtime.

## Local commands

From `src/api`, use the Maven Wrapper (a global `mvn` install is not required):

```bash
./mvnw clean test
./mvnw spring-boot:run
```

`spring-boot:run` expects PostgreSQL as in `application.yml` (default JDBC URL `localhost:5433`). For API and database together, use Compose from the repository root. See [docs/demo-commands.md](../../docs/demo-commands.md).

## Useful endpoints

```text
GET  /
GET  /api/tenders
GET  /api/tenders/{id}
POST /api/tenders
GET  /api/tenders/summary
GET  /actuator/health
GET  /actuator/health/liveness
GET  /actuator/health/readiness
GET  /actuator/info
GET  /actuator/metrics
GET  /actuator/prometheus
```

`application.yml` exposes `health` and `info` by default. Compose, Helm, and `k8s/base` set `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus` so metrics and Prometheus scraping work in the lab. Details: [docs/observability.md](../../docs/observability.md).
