# Security scanning and hardening

TenderOps Lab uses lightweight local security checks to demonstrate common DevSecOps controls.

## Current checks

The local security scan script runs:

- filesystem/dependency scan
- secret scan
- Docker image vulnerability scan
- Kubernetes/Helm manifest misconfiguration scan

Script:

```bash
./scripts/security/scan-local.sh
```

Reports are written to:

- reports/security/trivy-fs.txt
- reports/security/trivy-image.txt
- reports/security/trivy-k8s-config.txt

## Current hardening measures

The API container includes:

- non-root runtime user
- Kubernetes readiness probe
- Kubernetes liveness probe
- resource requests and limits
- configuration separated into ConfigMaps and Secrets
- private GitOps repository access configured through Argo CD repository credentials

The Kubernetes deployment includes:

- internal ClusterIP services
- API and database separated into different Deployments
- PostgreSQL persistent volume claim
- Helm-managed configuration
- Argo CD self-healing and pruning

## Actuator security note

This lab exposes only health, info, and metrics Actuator endpoints. In production, Actuator endpoints should be restricted because exposed management endpoints may reveal sensitive operational information.

## Future improvements

Possible next hardening steps:

- fail CI on critical vulnerabilities
- add container image signing
- use external secret management
- add NetworkPolicies
- add Pod security settings
- add Prometheus and Grafana
- add Loki for centralized logs
- add SBOM generation
