# TenderOps Lab

TenderOps Lab is a local-first DevOps portfolio project.

It demonstrates how a small Java/Spring Boot API can move through a modern delivery workflow:

```text
Spring Boot API
→ Maven build/test
→ Docker image
→ Docker Compose
→ Kubernetes
→ Helm
→ Argo CD GitOps
→ Actuator observability
→ Trivy security scanning
```

The business application is intentionally simple. The main purpose of the project is to demonstrate practical DevOps, Kubernetes, GitOps, observability, and security scanning concepts.

## What the application does

TenderOps Lab exposes a small tender-management API backed by PostgreSQL.

Main demo endpoint:

```bash
curl http://localhost:8080/api/tenders/summary
```

Expected response:

```json
{"service":"tenderops-api","tenderCount":2,"status":"running"}
```

## Implemented capabilities

- Java 21 / Spring Boot API
- Maven build and test workflow
- Docker multi-stage image build
- Docker Compose with PostgreSQL
- GitHub Actions CI workflow
- Local Kubernetes cluster with kind
- Kubernetes Deployments, Services, Secrets, ConfigMaps, PVCs, probes, and resource limits
- Helm chart for reusable deployment
- Argo CD GitOps deployment from GitHub
- Spring Boot Actuator health and metrics
- Trivy security scanning for dependencies, images, secrets, and Kubernetes configuration
- Kubernetes security hardening with non-root containers, dropped capabilities, read-only root filesystem, and seccomp profile

## Current delivery flow

```text
Developer changes code or config
  |
  v
Commit and push to GitHub
  |
  v
GitHub Actions runs CI checks
  |
  v
Argo CD reads desired state from Git
  |
  v
Helm chart is rendered
  |
  v
Kubernetes cluster is reconciled
  |
  v
API and PostgreSQL run in the tenderops namespace
```

The active deployment path is:

```text
Helm chart → Argo CD → Kubernetes
```

The raw Kubernetes manifests are kept as learning/reference material.

## Main demo commands

Check Argo CD:

```bash
kubectl get applications -n argocd
```

Check Kubernetes resources:

```bash
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
```

Port-forward the API:

```bash
kubectl port-forward -n tenderops service/tenderops-api 8080:8080
```

Test the API:

```bash
curl http://localhost:8080/api/tenders/summary
```

Check Actuator observability:

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

Run local CI checks:

```bash
./scripts/ci/api-check.sh
```

Run local security scans:

```bash
./scripts/security/scan-local.sh
```

## Repository structure

```text
src/api/              Spring Boot API
docker/               Docker-related notes
k8s/base/             Raw Kubernetes manifests
charts/tenderops/     Helm chart
gitops/apps/          Argo CD Application manifest
scripts/ci/           Local CI helper script
scripts/security/     Local Trivy security scan script
docs/                 Project documentation and interview notes
infra/                Reserved for future infrastructure-as-code examples
agents/               Reserved for future Cursor agent workflows
observability/        Reserved for future observability stack configuration
```

## Important documentation

- `docs/interview-demo.md`
- `docs/interview-prep-summary.md`
- `docs/architecture.md`
- `docs/project-goals.md`
- `docs/observability.md`
- `docs/security-hardening.md`
- `docs/learning-roadmap.md`
- `docs/security-agent-roadmap.md`

## Current project status

Completed:

- Spring Boot API
- Maven build/test
- Docker image build
- Docker Compose environment
- PostgreSQL persistence
- GitHub Actions CI
- local Kubernetes with kind
- raw Kubernetes manifests
- Helm chart
- Argo CD GitOps deployment
- Actuator health and metrics
- Trivy security scanning
- security remediation and Kubernetes hardening
- interview-focused documentation

Planned:

- Cursor security-agent workflows
- stricter CI security gates
- optional Prometheus/Grafana/Loki observability stack
- optional Terraform/Azure extension
- optional image registry flow

## Interview summary

TenderOps Lab is a local DevOps delivery lab for a Spring Boot API.

It uses Maven for build/test, Docker for packaging, Docker Compose for local multi-container development, kind for local Kubernetes, Helm for deployment packaging, Argo CD for GitOps delivery, Actuator for health and metrics, and Trivy for security scanning.

The project demonstrates the end-to-end path from code to container to Kubernetes deployment, including observability and security hardening.
