# Scripts

This directory contains helper scripts for local development, CI, and security scanning.

## CI script

```bash
./scripts/ci/api-check.sh
```

This script runs the local API validation workflow:

- Maven clean test
- Docker image build

It mirrors the main GitHub Actions CI workflow so the same basic checks can be run locally before pushing.

## Security script

```bash
./scripts/security/scan-local.sh
```

This script runs local Trivy security checks:

- filesystem and dependency scan
- secret scan
- Docker image vulnerability scan
- Helm-rendered Kubernetes configuration scan

Generated reports are written to:

```text
reports/security/
```

The generated reports are ignored by Git because they are local scan outputs.

## Intended use

Run these scripts from the repository root.
