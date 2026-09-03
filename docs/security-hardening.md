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

### Runtime secrets

The Helm chart no longer stores the database password in `values.yaml`.

Instead, the chart references pre-existing Kubernetes Secrets:

- `tenderops-api-runtime-secret`
- `tenderops-db-runtime-secret`

Create those Secrets in namespace `tenderops` before Argo CD sync. Exact `kubectl` commands: [charts/README.md](../charts/README.md) and [docs/demo-commands.md](demo-commands.md).

These are local demo credentials only.

The important security improvement is that the active Helm/GitOps path no longer derives runtime passwords from committed chart values.

In production, these Secrets should be created by an external secret-management workflow, such as:

- External Secrets Operator
- SOPS-encrypted values
- Sealed Secrets
- HashiCorp Vault
- Azure Key Vault
- CI/CD-controlled secret injection

### Container and Kubernetes hardening

The API container includes:

- multi-stage Docker build
- non-root runtime user (`10001`)
- Kubernetes readiness and liveness probes
- API resource requests and limits
- configuration separated into ConfigMaps and referenced Secrets
- private GitOps repository access configured through Argo CD repository credentials

The Kubernetes deployment includes:

- internal ClusterIP services
- API and database separated into different Deployments
- PostgreSQL persistent volume claim
- PostgreSQL resource requests and limits
- Helm-managed configuration
- Argo CD automated sync with self-healing and pruning
- NetworkPolicy restricting PostgreSQL ingress to API Pods on TCP 5432
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
- lab Actuator exposure: health, info, metrics, and prometheus (Compose, Helm, and `k8s/base` set this via `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`; `application.yml` defaults to health and info only)

## Local-lab acceptable limitations

These are acceptable for a kind/Docker Desktop portfolio lab:

- local demo credentials exist in manually created Kubernetes Secrets
- Docker Compose still uses demo credentials for local reproducibility
- no API authentication or authorization
- no Ingress or TLS termination
- local image tags loaded into kind instead of registry promotion
- Actuator exposed on the main application port for local inspection
- single-replica PostgreSQL with a PVC
- Argo CD using the `default` project with automated sync
- local `scan-local.sh` remains informational (`--exit-code 0`) while CI gates enforce HIGH/CRITICAL failures

## Production gaps

Required before any real production deployment:

- external secret management and secret rotation
- no manually created long-lived production Secrets
- registry-based image build, push, and promotion with immutable tags or digests
- API authentication and authorization
- managed or operable PostgreSQL backup/restore
- environment-specific Helm values and GitOps promotion controls
- centralized logs, dashboards, and alerting
- restricted Actuator exposure outside the cluster (auth, separate management network/port, reduced detail)
- image signing and SBOM generation
- tested incident and restore runbooks
- stronger GitOps guardrails, such as AppProjects, environment separation, and production approval gates

## Recommended next improvements

Short term:

1. Keep [charts/README.md](../charts/README.md) and [docs/demo-commands.md](demo-commands.md) aligned after any secret-model change.
2. Add a production-shaped secret-management option, such as SOPS, External Secrets, or Sealed Secrets.
3. Restrict Actuator detail and endpoints for non-local profiles.
4. Revisit the Flyway startup strategy and document why the custom migration runner is currently retained.

Medium term:

1. Pull GHCR images into kind and promote by digest/tag from CI.
2. Add PodDisruptionBudget and dedicated ServiceAccounts.
3. Document accepted residual risks after each remediation cycle.
4. Add environment-specific Helm values for local and prod-like deployments.

Later / optional portfolio enhancements:

1. Loki, Alertmanager rules, and tracing
2. Terraform/Azure infrastructure example
3. container image signing and SBOM generation
4. API authentication demo

## Accepted residual risks

Accepted for the current local-lab scope:

- local demo credentials are used in manually created Kubernetes Secrets
- Docker Compose still contains demo credentials for reproducible local development
- unauthenticated API surface in a non-public local cluster
- no Ingress/TLS because access is via local development paths
- no full monitoring/alerting stack
- no cloud secret manager or managed database
- local scan script does not fail the shell process on findings; CI security workflow enforces gates instead

These residual risks are intentional for a learning/demo environment and must not be treated as production-approved exceptions.

## Actuator security note

This lab exposes health, info, metrics, and Prometheus Actuator endpoints on the application port and currently shows health details for local inspection.

In production, Actuator endpoints should be restricted because exposed management endpoints may reveal sensitive operational information. A production-style deployment should reduce exposed endpoints, avoid `show-details: always`, and protect management endpoints through authentication, network isolation, or a separate management path.
