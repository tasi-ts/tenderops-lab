# TenderOps API

Minimal Java Spring Boot API service for the TenderOps Lab project.

## Purpose

This service provides a small tendering-domain API that can be used to practice:

- Maven builds
- Docker image creation
- CI/CD pipelines
- Kubernetes deployments
- Health checks
- Observability
- Security review workflows

## Local commands

From `src/api`:

```bash
mvn clean test
mvn spring-boot:run
```

## Useful Endpoints

```bash
GET  /
GET  /api/tenders
GET  /api/tenders/{id}
POST /api/tenders
GET  /api/tenders/summary
GET  /actuator/health
GET  /actuator/health/liveness
GET  /actuator/health/readiness
```
