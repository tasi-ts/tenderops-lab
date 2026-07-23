# TenderOps Lab — Interview Demo Guide

## One-minute summary

TenderOps Lab is a local-first DevOps portfolio project that demonstrates how a small Java/Spring Boot API can move through a modern delivery path:

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

The goal of the project is not business complexity. The goal is to demonstrate practical DevOps workflow knowledge: build automation, containerization, Kubernetes deployment, GitOps, observability, and security scanning.

## What the application does

The application is a simple tender-management API backed by PostgreSQL.

Key endpoint:

```bash
curl http://localhost:8080/api/tenders/summary
```

Expected result:

```json
{"service":"tenderops-api","tenderCount":2,"status":"running"}
```

## Demo flow

### 1. Show CI/build foundation

```bash
./scripts/ci/api-check.sh
```

Explain:

- Maven runs automated tests.
- Docker builds the API image.
- GitHub Actions runs the equivalent check on push/PR.

### 2. Show Kubernetes status

```bash
kubectl get nodes
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
```

Explain:

- The local Kubernetes cluster runs through kind.
- The API has 2 replicas.
- PostgreSQL runs as a separate deployment.
- Argo CD reports the app as Synced and Healthy.

### 3. Show GitOps

```bash
code charts/tenderops/values.yaml
```

Explain:

- Desired deployment state lives in Git.
- Argo CD watches the Helm chart from GitHub.
- Changing `api.replicas` and pushing to `main` causes Argo CD to reconcile the cluster.

### 4. Show observability

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

Explain:

- Spring Boot Actuator exposes health and runtime metrics.
- Kubernetes uses liveness/readiness probes.
- Metrics could later be scraped by Prometheus.

### 5. Show security scanning

```bash
./scripts/security/scan-local.sh
```

Explain:

- Trivy scans dependencies, secrets, image vulnerabilities, and Kubernetes misconfigurations.
- Findings were remediated by upgrading dependencies, changing the runtime image, and adding Kubernetes security contexts.

## Architecture summary

```text
Developer machine
  |
  | git push
  v
GitHub repository
  |
  | GitHub Actions
  v
CI: Maven test + Docker build

Local Kubernetes cluster via kind
  |
  | Argo CD watches GitHub
  v
Helm chart deployed into tenderops namespace
  |
  +-- tenderops-api Deployment, Service, Actuator endpoints
  |
  +-- tenderops-db Deployment, Service, PVC
```

## Key interview talking points

### Why Docker?

Docker packages the application and runtime into a repeatable container image. This avoids relying on machine-specific setup.

### Why Kubernetes?

Kubernetes manages containerized workloads: deployments, services, probes, scaling, restart behavior, and configuration.

### Why Helm?

Helm turns repeated Kubernetes YAML into a reusable, configurable deployment package.

### Why Argo CD?

Argo CD demonstrates GitOps. Git becomes the source of truth, and Argo CD continuously reconciles the cluster to match the desired state.

### Why Actuator?

Actuator provides application-level health and metrics. Kubernetes can use health endpoints for readiness and liveness probes.

### Why Trivy?

Trivy gives a practical DevSecOps control by scanning dependencies, container images, secrets, and Kubernetes misconfigurations.

## What I would improve next

- Add Prometheus and Grafana.
- Add Loki or another centralized logging stack.
- Add CI security gates for critical findings.
- Publish images to a registry instead of loading them locally into kind.
- Add Terraform for Azure infrastructure.
- Add separate dev/stage values files.
- Add NetworkPolicies and external secrets.
