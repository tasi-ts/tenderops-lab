# Observability

TenderOps Lab uses a lightweight observability setup suitable for local Kubernetes practice.

## Application health

The Spring Boot API exposes Actuator health endpoints:

- `/actuator/health`
- `/actuator/health/liveness`
- `/actuator/health/readiness`

Kubernetes uses these endpoints for liveness and readiness probes.

## Application metrics

The API exposes basic Actuator metrics through:

- `/actuator/metrics`
- `/actuator/metrics/jvm.memory.used`

These metrics are useful for local inspection and can later be scraped by Prometheus.

## Kubernetes troubleshooting commands

```bash
kubectl get pods -n tenderops
kubectl describe deployment tenderops-api -n tenderops
kubectl logs -n tenderops -l app=tenderops-api --tail=100
kubectl logs -n tenderops -l app=tenderops-db --tail=100
kubectl get events -n tenderops --sort-by=.metadata.creationTimestamp
```

## Current observability scope

This lab currently demonstrates:

- application health checks
- Kubernetes liveness/readiness probes
- application runtime metrics
- container logs
- Kubernetes events

A future extension could add Prometheus, Grafana, Loki, or OpenTelemetry.
