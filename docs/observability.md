# Observability

TenderOps Lab uses a local Kubernetes observability setup based on Spring Boot Actuator, Prometheus, and Grafana.

## Current scope

The current observability setup covers metrics and basic Kubernetes runtime inspection.

Implemented:

- Spring Boot Actuator health endpoints
- Spring Boot Actuator JSON metrics endpoint
- Spring Boot Actuator Prometheus metrics endpoint
- Kubernetes liveness and readiness probes
- Prometheus/Grafana stack in the local kind cluster
- ServiceMonitor-based scraping of the TenderOps API
- Basic PromQL queries for API and JVM visibility
- Container logs and Kubernetes event inspection

Not yet implemented:

- Centralized application logs
- Loki
- Alertmanager rules
- Production-grade Grafana dashboards
- External monitoring integrations
- OpenTelemetry tracing

## Application health

The Spring Boot API exposes Actuator health endpoints:

- `/actuator/health`
- `/actuator/health/liveness`
- `/actuator/health/readiness`

Kubernetes uses these endpoints for liveness and readiness probes.

## Application metrics

The API exposes JSON Actuator metrics through:

- `/actuator/metrics`
- `/actuator/metrics/jvm.memory.used`

These endpoints are useful for direct local inspection.

The API also exposes Prometheus-format metrics through:

- `/actuator/prometheus`

This endpoint is intended for Prometheus scraping.

## Local Kubernetes access

TenderOps API port-forward:

```bash
kubectl port-forward -n tenderops svc/tenderops-api 8080:8080
```

Prometheus port-forward:

```bash
kubectl port-forward -n monitoring svc/monitoring-kube-prometheus-prometheus 9090:9090
```

Grafana port-forward:

```bash
kubectl port-forward -n monitoring svc/monitoring-grafana 3000:80
```

Grafana local credentials:

```text
username: admin
password: admin
```

These credentials are local-lab only.

## Prometheus target verification

Prometheus targets can be checked through the Prometheus API:

```bash
curl -s http://localhost:9090/api/v1/targets \
  | jq -r '
    .data.activeTargets[]
    | [.health, .labels.namespace, .labels.service, .labels.job, .scrapeUrl, .lastError]
    | @tsv
  ' | sort
```

Expected TenderOps target:

```text
up    tenderops    tenderops-api    ...
```

A direct Prometheus query can also verify that TenderOps is being scraped:

```bash
curl -s --get http://localhost:9090/api/v1/query \
  --data-urlencode 'query=up{namespace="tenderops", service="tenderops-api"}' | jq
```

Expected result value:

```text
1
```

## Useful PromQL queries

API scrape health:

```promql
up{namespace="tenderops", service="tenderops-api"}
```

HTTP request rate:

```promql
sum(rate(http_server_requests_seconds_count{namespace="tenderops"}[5m]))
```

HTTP request rate by URI:

```promql
sum by (uri) (
  rate(http_server_requests_seconds_count{namespace="tenderops"}[5m])
)
```

Average HTTP request duration:

```promql
sum(rate(http_server_requests_seconds_sum{namespace="tenderops"}[5m]))
/
sum(rate(http_server_requests_seconds_count{namespace="tenderops"}[5m]))
```

JVM memory used:

```promql
sum(jvm_memory_used_bytes{namespace="tenderops"})
```

Process CPU usage:

```promql
process_cpu_usage{namespace="tenderops"}
```

## Generate sample API traffic

To make API metrics easier to see in Prometheus or Grafana, generate a small amount of local traffic:

```bash
for i in {1..20}; do
  curl -s http://localhost:8080/api/tenders > /dev/null
  curl -s http://localhost:8080/actuator/health > /dev/null
done
```

Wait 15-30 seconds, then query Prometheus again.

## Kubernetes troubleshooting commands

```bash
kubectl get pods -n tenderops
kubectl describe deployment tenderops-api -n tenderops
kubectl logs -n tenderops -l app=tenderops-api --tail=100
kubectl logs -n tenderops -l app=tenderops-db --tail=100
kubectl get events -n tenderops --sort-by=.metadata.creationTimestamp
```

Monitoring stack troubleshooting:

```bash
kubectl get pods -n monitoring
kubectl get svc -n monitoring
kubectl get servicemonitor -A
kubectl get prometheus -n monitoring
```

## Kubernetes image validation habit

After significant API, Dockerfile, dependency, runtime, or Actuator changes, rebuild and reload the local image into kind:

```bash
docker build -t tenderops-api:0.1.0 ./src/api

kind load docker-image tenderops-api:0.1.0 --name tenderops

kubectl rollout restart deployment/tenderops-api -n tenderops
kubectl rollout status deployment/tenderops-api -n tenderops
```

Then verify:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/prometheus | head -20
curl http://localhost:8080/api/tenders | jq
```

## Security note for Actuator exposure

Actuator endpoints are useful for local troubleshooting, but they can reveal operational detail.

This lab exposes health, info, metrics, and Prometheus metrics for local inspection and Prometheus scraping. It also shows health details in the local environment.

That is acceptable for a local port-forward demo.

In production, Actuator should be:

- authenticated
- network-restricted
- configured with reduced health detail
- separated from public application traffic where appropriate
- monitored through controlled observability infrastructure

## Production observability gaps

For production, the project would need:

- authenticated and restricted observability endpoints
- TLS/Ingress controls
- alerting rules
- dashboard provisioning
- log aggregation
- retention settings
- environment-specific monitoring configuration
- OpenTelemetry tracing if distributed request visibility is required
- documented incident and troubleshooting runbooks
