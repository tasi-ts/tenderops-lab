# TenderOps Lab — DevOps Portfolio Summary

TenderOps Lab is a local-first DevOps and Kubernetes practice project. It demonstrates how a small Spring Boot API can be built, containerized, deployed, observed, and scanned using common DevOps tooling.

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

## Main demo commands

```bash
./scripts/ci/api-check.sh
kubectl get applications -n argocd
kubectl get pods -n tenderops
kubectl get deployment tenderops-api -n tenderops
curl http://localhost:8080/api/tenders/summary
curl http://localhost:8080/actuator/metrics
./scripts/security/scan-local.sh
```

## Delivery flow

```
Code
→ Maven test
→ Docker image
→ Kubernetes manifests
→ Helm chart
→ Argo CD GitOps sync
→ Actuator observability
→ Trivy security scanning
```

## Interview demo guide

See:
`docs/interview-demo.md`
