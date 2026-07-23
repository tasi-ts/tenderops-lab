# Observability

This directory is reserved for observability-related configuration.

The current project uses lightweight Spring Boot and Kubernetes observability rather than a full monitoring stack.

## Current observability

Implemented:

- Spring Boot Actuator health endpoint
- Spring Boot Actuator metrics endpoint
- Kubernetes readiness probe
- Kubernetes liveness probe
- `kubectl logs`
- `kubectl describe`
- `kubectl get events`

## Current documentation

See:

```text
docs/observability.md
```

## Useful commands

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
kubectl logs -n tenderops -l app=tenderops-api --tail=100
kubectl get events -n tenderops --sort-by=.metadata.creationTimestamp
```

## Future extensions

Potential future additions:

- Prometheus
- Grafana
- Loki
- OpenTelemetry
- dashboards
- alerting rules
