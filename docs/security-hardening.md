# Security scanning and hardening

TenderOps Lab uses local and CI security checks to demonstrate common DevSecOps controls.

This project is a local-first portfolio lab. It is not production-ready.

## Implemented security controls

### Local scanning

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

- `reports/security/trivy-fs.txt`
- `reports/security/trivy-image.txt`
- `reports/security/trivy-k8s-config.txt`

Generated reports are gitignored.

### CI security gates

GitHub Actions security gates are implemented in `.github/workflows/security.yml`.

On pushes to `main` and on pull requests, the workflow:

- builds the API Docker image
- runs `helm lint charts/tenderops`
- renders the Helm chart to Kubernetes manifests
- runs Trivy filesystem scan (`vuln`, `secret`, `misconfig`)
- runs Trivy image scan
- runs Trivy rendered Kubernetes config scan
- fails the job on HIGH/CRITICAL findings (`exit-code: 1`)
- uploads Trivy reports as a GitHub Actions artifact (`trivy-security-reports`, 14-day retention)

This is separate from the functional CI workflow in `.github/workflows/ci.yml`, which still runs Maven tests and Docker image build.

### Container and Kubernetes hardening

The API container includes:

- multi-stage Docker build
- non-root runtime user (`10001`)
- Kubernetes readiness and liveness probes
- API resource requests and limits
- configuration separated into ConfigMaps and Secrets
- private GitOps repository access configured through Argo CD repository credentials

The Kubernetes deployment includes:

- internal ClusterIP services
- API and database separated into different Deployments
- PostgreSQL persistent volume claim
- Helm-managed configuration
- Argo CD automated sync with self-healing and pruning
- pod and container security contexts:
  - `runAsNonRoot: true`
  - `allowPrivilegeEscalation: false`
  - `readOnlyRootFilesystem: true`
  - `capabilities.drop: ALL`
  - `seccompProfile: RuntimeDefault`
  - writable emptyDir mounts where required (`/tmp`, PostgreSQL runtime paths)

### Application security baseline

- Spring Validation on create requests (`@Valid`, `@NotBlank`)
- Flyway-managed schema migrations in source control
- limited Actuator exposure: health, info, and metrics

## Local-lab acceptable limitations

These are acceptable for a kind/Docker Desktop portfolio lab:

- demo database credentials committed in Helm values / Compose for reproducibility
- no API authentication or authorization
- no NetworkPolicies
- no Ingress or TLS termination
- local image tags loaded into kind instead of registry promotion
- Actuator exposed on the main application port for local inspection
- single-replica PostgreSQL with a PVC
- Argo CD using the `default` project with automated sync
- local `scan-local.sh` remains informational (`--exit-code 0`) while CI gates enforce HIGH/CRITICAL failures

## Production gaps

Required before any real production deployment:

- external secret management and no plaintext credentials in Git
- NetworkPolicies and stronger cluster network isolation
- registry-based image build, push, and promotion with immutable tags or digests
- API authentication and authorization
- managed or operable PostgreSQL backup/restore
- environment-specific Helm values and GitOps promotion controls
- centralized logs, dashboards, and alerting
- restricted Actuator exposure outside the cluster (auth, separate management network/port, reduced detail)
- image signing and SBOM generation
- PostgreSQL resource requests and limits
- tested incident and restore runbooks

## Recommended next improvements

Short term:

1. Add NetworkPolicies for API ↔ PostgreSQL traffic.
2. Add PostgreSQL resource requests and limits.
3. Consolidate Flyway execution so only one migration path runs at startup.
4. Restrict Actuator detail and endpoints for non-local profiles.

Medium term:

1. Move secrets out of committed values (External Secrets, SOPS, or equivalent).
2. Publish images to a registry and promote by digest/tag from CI.
3. Document accepted residual risks after each remediation cycle.
4. Add PodDisruptionBudget and dedicated ServiceAccounts.

Later / optional portfolio enhancements:

1. Prometheus/Grafana/Loki observability stack
2. Terraform/Azure infrastructure example
3. container image signing and SBOM generation
4. API authentication demo

## Accepted residual risks

Accepted for the current local-lab scope:

- demo credentials in Git and rendered manifests for local reproducibility
- unauthenticated API surface in a non-public local cluster
- no NetworkPolicies while access is primarily via `kubectl port-forward`
- no full monitoring/alerting stack
- no cloud secret manager or managed database
- local scan script does not fail the shell process on findings; CI security workflow enforces gates instead

These residual risks are intentional for a learning/demo environment and must not be treated as production-approved exceptions.

## Actuator security note

This lab exposes health, info, and metrics Actuator endpoints and currently shows health details. In production, Actuator endpoints should be restricted because exposed management endpoints may reveal sensitive operational information.
