# Docker

This folder documents Docker-related usage for TenderOps Lab.

## Build the API image directly

```bash
docker build -t tenderops-api:0.1.0 ./src/api
```

## Run the API container directly

```bash
docker run --rm \
  --name tenderops-api \
  -p 8080:8080 \
  tenderops-api:0.1.0
```

## Run with Docker Compose

From the repository root:

```bash
docker compose up --build
```

Useful endpoints:

```bash
GET /api/tenders
GET /api/tenders/summary
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
```

Stop the stack:

```bash
docker compose down
```
