# CI/CD

TenderOps Lab uses GitHub Actions for continuous integration and security checks.

This is a local-first lab. CI validates code and security posture; deployment is performed by Argo CD from Git, not by pushing images from CI.

## Functional CI

Workflow: `.github/workflows/ci.yml`

Runs on pushes to `main` and on pull requests.

Current checks:

- Java setup with Temurin 21
- Maven test execution for the API service
- Docker image build for the API service

### Local equivalent

```bash
./scripts/ci/api-check.sh
```

## CI security gates

Workflow: `.github/workflows/security.yml`

Runs on pushes to `main` and on pull requests.

Current checks:

- Docker image build for the API
- Helm lint (`helm lint charts/tenderops`)
- Helm template render for Kubernetes config scanning
- Trivy filesystem scan (`vuln`, `secret`, `misconfig`)
- Trivy image scan
- Trivy rendered Kubernetes config scan
- fail on HIGH/CRITICAL findings
- upload `reports/security/` as the `trivy-security-reports` artifact (14-day retention)

### Local equivalent

```bash
./scripts/security/scan-local.sh
helm lint charts/tenderops
```

Note: the local scan script writes reports but uses a non-failing exit code for interactive use. The GitHub Actions security workflow enforces failure on HIGH/CRITICAL findings.

## Delivery boundary

```text
GitHub Actions
  |-- ci.yml          → tests + image build
  |-- security.yml    → Trivy gates + Helm lint
  |
  v
Git remains source of truth
  |
  v
Argo CD syncs Helm chart into Kubernetes
```

CI does not currently publish images to a registry or deploy to the cluster.

## Planned additions

- image registry publish and promotion
- deployment dry-runs against rendered manifests
- optional GitLab CI / Jenkins examples for portfolio comparison
