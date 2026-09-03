# Project Walkthrough

This is the public demo narrative for TenderOps Lab. Use it with [docs/demo-commands.md](demo-commands.md) when walking through the repository.

## One-minute overview

TenderOps Lab is a local-first DevOps lab around a small Java/Spring Boot API and PostgreSQL.

The application is intentionally simple. The delivery path is the point of the project:

```text
Spring Boot API
→ Maven build/test
→ Docker image
→ Docker Compose
→ kind Kubernetes
→ Helm
→ Argo CD GitOps
→ Trivy security scanning
→ Prometheus / Grafana
```

The lab is portfolio-ready. It is not production-ready.

## What problem the lab simulates

Teams still need a realistic path from source to a running service:

- build and test the same way locally and in CI
- package a JVM service as a container
- run it with a real database
- deploy with Kubernetes objects that include probes, resources, and hardening
- treat Git as the desired state for the cluster
- scan dependencies, images, and manifests
- scrape metrics and inspect them in Prometheus and Grafana

TenderOps uses a tiny tender-management API so those platform concerns stay visible.

## Architecture walkthrough

Two execution paths share the same API image:

1. **Docker Compose** — API and PostgreSQL on the developer machine.
2. **kind + Helm + Argo CD** — the same workloads in Kubernetes, synced from Git.

GitHub Actions runs tests, Trivy gates, and GHCR publish. CI does not deploy. Argo CD deploys.

The kind demo still uses `tenderops-api:0.1.0` loaded with `kind load` unless GHCR is public or the cluster has pull credentials.

Diagrams: [docs/architecture.md](architecture.md).

## Local development path

From the repository root:

```bash
docker compose up --build -d
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/tenders/summary
```

Explain:

- Compose is the fast inner loop.
- Demo database credentials in `compose.yaml` are local-only.
- Maven tests and an image build can be run with `./scripts/ci/api-check.sh`.

Stop with `docker compose down`.

## Kubernetes and GitOps path

Show cluster and GitOps status:

```bash
kubectl get nodes
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
```

Explain:

- kind provides the local cluster.
- The API runs two replicas; PostgreSQL runs one Pod with a PVC.
- Runtime Secrets must exist before the app can become healthy.
- Argo CD should report Synced and Healthy.

Show GitOps:

- Desired state lives in `charts/tenderops`.
- `gitops/apps/tenderops-application.yaml` points Argo CD at GitHub `main`.
- Changing `api.replicas` and pushing to `main` is the usual reconciliation demo.

Image habit after API or Dockerfile changes:

```bash
docker build -t tenderops-api:0.1.0 ./src/api
kind load docker-image tenderops-api:0.1.0 --name tenderops
kubectl rollout restart deployment/tenderops-api -n tenderops
kubectl rollout status deployment/tenderops-api -n tenderops
kubectl port-forward -n tenderops svc/tenderops-api 8080:8080
```

## Security controls

```bash
./scripts/security/scan-local.sh
```

Explain:

- Local Trivy covers filesystem, secrets, the API image, and Helm-rendered Kubernetes config.
- `.github/workflows/security.yml` fails on HIGH/CRITICAL findings and lints the chart.
- Hardening includes non-root containers, dropped capabilities, read-only root filesystem, and seccomp `RuntimeDefault`.
- Helm does not commit the database password in `values.yaml`.
- A NetworkPolicy limits PostgreSQL ingress to API Pods on TCP 5432.

Details: [docs/security-hardening.md](security-hardening.md).

## Observability controls

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus | head -20
```

Explain:

- Kubernetes probes use Actuator liveness and readiness.
- Prometheus scrapes `/actuator/prometheus` through a ServiceMonitor.
- Grafana is the local kube-prometheus-stack UI, with a TenderOps API dashboard ConfigMap.

Prometheus and Grafana port-forwards and target checks: [docs/observability.md](observability.md) and [docs/demo-commands.md](demo-commands.md).

## Operational validation flow

Suggested order for a live demo:

```text
1. README one-minute overview
2. Compose or kind API call
3. GitHub Actions CI and security workflows
4. kubectl pods and Deployment in tenderops
5. Argo CD Synced / Healthy
6. Actuator health and Prometheus metrics
7. Prometheus targets and Grafana dashboard
8. Trivy local scan plus CI gate distinction
9. Local-only assumptions and production gaps
```

Useful command sequence:

```bash
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
curl http://localhost:8080/api/tenders/summary
curl http://localhost:8080/actuator/health
./scripts/security/scan-local.sh
```

## Known limitations

- Local demo credentials and Grafana `admin` / `admin`
- No API authentication
- No Ingress or TLS
- Actuator detail exposed for local inspection
- kind still depends on local image load unless GHCR is pullable
- No Loki, Alertmanager rules, or tracing
- No PostgreSQL backup/restore drill
- Terraform/Azure is out of scope for this version

The project is a controlled lab, not a production platform.

## Possible next extensions

- Public GHCR pull into kind, then promote by digest
- External secret management (SOPS, External Secrets, or Sealed Secrets)
- Loki and alerting
- API authentication
- Stronger GitOps environment separation
- Optional Terraform/Azure infrastructure as a later segment

Project talking points and Q&A: [docs/project-review-summary.md](project-review-summary.md).
