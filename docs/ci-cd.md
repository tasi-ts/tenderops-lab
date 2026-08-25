# CI/CD

TenderOps Lab uses GitHub Actions for continuous integration, security checks, and container image publishing.

This is a local-first lab. CI validates code and security posture, and publishes the API image to GitHub Container Registry. Deployment is still performed by Argo CD from Git, not directly by CI.

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

## Image publishing

Workflow: `.github/workflows/image-publish.yml`

Runs on pushes to `main` and can also be started manually with `workflow_dispatch`.

Current behavior:

- builds the API Docker image from `src/api/Dockerfile`
- logs in to GitHub Container Registry (`ghcr.io`)
- publishes the API image to GHCR
- tags the image with:
  - `main`
  - `sha-<commit>`

Current published image pattern:

```text
ghcr.io/tasi-ts/tenderops-api:main
ghcr.io/tasi-ts/tenderops-api:sha-<commit>
```

The active local kind deployment still uses the locally built image:

```text
tenderops-api:0.1.0
```

This is intentional for now. The GHCR publishing workflow is implemented, but the local kind cluster cannot yet pull the GHCR image unless one of these is true:

- the GHCR package is public, or
- the cluster has a Kubernetes `imagePullSecret` for GHCR.

For the future public portfolio path, the preferred option is to make the GHCR package public so the demo cluster can pull the image without requiring local pull credentials.

## Delivery boundary

```text
GitHub Actions
  |-- ci.yml                 → tests + image build
  |-- security.yml           → Trivy gates + Helm lint
  |-- image-publish.yml      → publish API image to GHCR
  |
  v
Git remains source of truth
  |
  v
Argo CD syncs Helm chart into Kubernetes
```

CI publishes the API image, but it does not currently update Helm values, promote image tags, or deploy directly to the cluster.

Argo CD remains responsible for syncing the desired Kubernetes state from Git.

## Planned additions

- image promotion by immutable tag or digest
- optional public GHCR pull path for the portfolio version
- deployment dry-runs against rendered manifests
- optional GitLab CI / Jenkins examples for portfolio comparison
