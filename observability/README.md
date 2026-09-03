# Observability

This directory holds local Kubernetes monitoring values for TenderOps Lab.

The live operational story is documented in `docs/observability.md`.

## Current observability

Implemented:

- Spring Boot Actuator health, liveness, and readiness
- Spring Boot Actuator JSON metrics
- Spring Boot Actuator Prometheus endpoint
- Kubernetes readiness and liveness probes
- kube-prometheus-stack values for a local kind install
- ServiceMonitor-based scrape of the TenderOps API (defined in the Helm chart)
- Grafana dashboard ConfigMap for the API (defined in the Helm chart)
- `kubectl logs`, `kubectl describe`, and `kubectl get events`

Not yet implemented:

- Loki / centralized application logs
- Alertmanager rules
- OpenTelemetry tracing
- authenticated or production-grade Grafana

## Files

```text
observability/kube-prometheus-stack-values.yaml
```

Local-only Grafana credentials in that file are `admin` / `admin`. Do not reuse them outside this lab.

Install example (kind cluster already running):

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  -f observability/kube-prometheus-stack-values.yaml
```

Expected result: Prometheus and Grafana become Ready in namespace `monitoring`. Service names used in docs:

```text
monitoring-kube-prometheus-prometheus
monitoring-grafana
```

## Useful commands

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus | head -20
kubectl logs -n tenderops -l app=tenderops-api --tail=100
kubectl get events -n tenderops --sort-by=.metadata.creationTimestamp
```

Port-forwards and PromQL examples: `docs/observability.md` and `docs/demo-commands.md`.
